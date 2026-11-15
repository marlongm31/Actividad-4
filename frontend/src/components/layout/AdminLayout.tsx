import { useEffect, useState } from 'react';
import { Link, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import {
  CalendarDays,
  Clock,
  LayoutDashboard,
  ListChecks,
  LogOut,
  Menu,
  Scissors,
  X,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import { Logo } from '../ui/Logo';

const NAV_ITEMS = [
  { to: '/admin', label: 'Resumen', icon: LayoutDashboard, end: true },
  { to: '/admin/citas', label: 'Citas', icon: ListChecks },
  { to: '/admin/servicios', label: 'Servicios', icon: Scissors },
  { to: '/admin/horarios', label: 'Horarios', icon: Clock },
];

export function AdminLayout() {
  const { session, logout } = useAuth();
  const { notify } = useToast();
  const navigate = useNavigate();
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    setMenuOpen(false);
  }, [location.pathname]);

  const handleLogout = () => {
    logout();
    notify('Sesion cerrada correctamente', 'info');
    navigate('/admin/login', { replace: true });
  };

  return (
    <div className="admin-shell">
      <aside className={`admin-sidebar ${menuOpen ? 'is-open' : ''}`}>
        <div className="admin-sidebar__top">
          <Link to="/admin" className="brand-link">
            <Logo compact />
          </Link>
          <span className="admin-sidebar__tag">Panel</span>
        </div>

        <nav className="admin-nav" aria-label="Navegacion administrativa">
          {NAV_ITEMS.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) => (isActive ? 'is-active' : '')}
              >
                <Icon size={18} />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>

        <div className="admin-sidebar__bottom">
          <Link to="/" className="admin-sidebar__public">
            <CalendarDays size={16} />
            Ver sitio publico
          </Link>
          <div className="admin-user">
            <div className="admin-user__avatar">
              {(session?.name ?? 'A').charAt(0).toUpperCase()}
            </div>
            <div className="admin-user__info">
              <strong>{session?.name ?? 'Administrador'}</strong>
              <span>{session?.email}</span>
            </div>
          </div>
          <button type="button" className="admin-logout" onClick={handleLogout}>
            <LogOut size={16} />
            Cerrar sesion
          </button>
        </div>
      </aside>

      {menuOpen ? (
        <button
          type="button"
          className="admin-backdrop"
          aria-label="Cerrar menu"
          onClick={() => setMenuOpen(false)}
        />
      ) : null}

      <div className="admin-content">
        <header className="admin-topbar">
          <button
            type="button"
            className="menu-toggle menu-toggle--dark"
            aria-label="Abrir menu"
            onClick={() => setMenuOpen((current) => !current)}
          >
            {menuOpen ? <X size={22} /> : <Menu size={22} />}
          </button>
          <Logo compact />
          <Link to="/admin/citas" className="admin-topbar__cta">
            <ListChecks size={16} />
            Citas
          </Link>
        </header>
        <main className="admin-main">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
