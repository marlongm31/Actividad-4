import { useEffect, useState } from 'react';
import { Link, NavLink, Outlet, useLocation } from 'react-router-dom';
import { CalendarCheck, Menu, Phone, Scissors, X } from 'lucide-react';
import { Logo } from '../ui/Logo';

export function PublicLayout() {
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();

  useEffect(() => {
    setMenuOpen(false);
  }, [location.pathname]);

  return (
    <div className="public-shell">
      <header className="public-header">
        <div className="container public-header__inner">
          <Link to="/" className="brand-link" aria-label="BarberTurn inicio">
            <Logo />
          </Link>

          <nav className={`public-nav ${menuOpen ? 'is-open' : ''}`} aria-label="Navegacion principal">
            <NavLink to="/" end onClick={() => setMenuOpen(false)}>
              Inicio
            </NavLink>
            <Link to="/#servicios" onClick={() => setMenuOpen(false)}>
              Servicios
            </Link>
            <Link to="/#como-funciona" onClick={() => setMenuOpen(false)}>
              Como funciona
            </Link>
            <Link to="/admin/login" className="public-nav__admin">
              Acceso peluquero
            </Link>
            <Link to="/reservar" className="public-nav__cta" onClick={() => setMenuOpen(false)}>
              <CalendarCheck size={17} />
              Reservar cita
            </Link>
          </nav>

          <button
            type="button"
            className="menu-toggle"
            aria-label={menuOpen ? 'Cerrar menu' : 'Abrir menu'}
            aria-expanded={menuOpen}
            onClick={() => setMenuOpen((current) => !current)}
          >
            {menuOpen ? <X size={22} /> : <Menu size={22} />}
          </button>
        </div>
      </header>

      <main className="public-main">
        <Outlet />
      </main>

      <footer className="public-footer">
        <div className="container public-footer__grid">
          <div className="public-footer__brand">
            <Logo />
            <p>
              Cortes con caracter, reservas sin complicaciones. Tu tiempo y tu estilo en un solo
              lugar.
            </p>
          </div>
          <div>
            <h4>Horario</h4>
            <p>Lunes a viernes: 9:00 a. m. - 7:00 p. m.</p>
            <p>Sabado: 9:00 a. m. - 6:00 p. m.</p>
            <p>Domingo: cerrado</p>
          </div>
          <div>
            <h4>Contacto</h4>
            <p className="footer-contact">
              <Phone size={15} /> +57 300 123 4567
            </p>
            <p className="footer-contact">
              <Scissors size={15} /> Calle 10 # 5-42, Colombia
            </p>
            <Link to="/reservar" className="footer-link">
              Reservar una cita
            </Link>
          </div>
        </div>
        <div className="container public-footer__bottom">
          <span>© {new Date().getFullYear()} BarberTurn. Todos los derechos reservados.</span>
          <div className="public-footer__actions">
            <Link to="/reservar">Reservar</Link>
            <Link to="/admin/login">Administracion</Link>
          </div>
        </div>
      </footer>
    </div>
  );
}
