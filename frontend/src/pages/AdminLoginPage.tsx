import { useState } from 'react';
import type { FormEvent } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { ArrowLeft, ArrowRight, CalendarCheck, LockKeyhole, Mail, ShieldCheck, Sparkles } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { normalizeError } from '../utils/errors';
import { Button } from '../components/ui/Button';
import { FormField } from '../components/ui/FormField';
import { Logo } from '../components/ui/Logo';

export function AdminLoginPage() {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  if (isAuthenticated) {
    return (
      <div className="login-page">
        <div className="container login-page__inner">
          <div className="login-card login-card--center">
            <ShieldCheck size={38} />
            <h1>Ya tienes una sesion activa</h1>
            <p>Puedes continuar directamente al panel administrativo.</p>
            <div className="login-card__actions">
              <Link to="/admin">
                <Button icon={<ArrowRight size={17} />}>Ir al panel</Button>
              </Link>
              <Link to="/">
                <Button variant="ghost" icon={<ArrowLeft size={17} />}>
                  Volver al sitio
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login(email.trim(), password);
      const state = location.state as { from?: string } | null;
      navigate(state?.from ?? '/admin', { replace: true });
    } catch (caught) {
      setError(normalizeError(caught).message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-page__inner">
        <aside className="login-aside">
          <Link to="/" className="brand-link">
            <Logo />
          </Link>
          <div className="login-aside__content">
            <span className="eyebrow eyebrow--light">
              <Sparkles size={14} />
              Panel del peluquero
            </span>
            <h1>Tu agenda, siempre bajo control.</h1>
            <p>
              Consulta las reservas del dia, confirma citas, administra tus servicios y ajusta tus
              horarios desde un solo lugar.
            </p>
            <ul className="login-aside__list">
              <li>
                <CalendarCheck size={17} />
                Agenda diaria en tiempo real
              </li>
              <li>
                <ShieldCheck size={17} />
                Acceso privado con sesion segura
              </li>
              <li>
                <Sparkles size={17} />
                Servicios y horarios a tu medida
              </li>
            </ul>
          </div>
          <Link to="/" className="login-aside__back">
            <ArrowLeft size={16} />
            Volver al sitio publico
          </Link>
        </aside>

        <section className="login-panel">
          <div className="login-card">
            <div className="login-card__heading">
              <span className="login-card__icon">
                <LockKeyhole size={20} />
              </span>
              <h2>Iniciar sesion</h2>
              <p>Ingresa con tu cuenta de administrador para gestionar BarberTurn.</p>
            </div>

            <form onSubmit={(event) => void submit(event)} className="login-form" noValidate>
              <FormField label="Correo electronico" htmlFor="email" required>
                <div className="input-with-icon">
                  <Mail size={16} />
                  <input
                    id="email"
                    type="email"
                    autoComplete="email"
                    value={email}
                    placeholder="admin@barberturn.com"
                    onChange={(event) => setEmail(event.target.value)}
                    required
                  />
                </div>
              </FormField>

              <FormField label="Contrasena" htmlFor="password" required>
                <div className="input-with-icon">
                  <LockKeyhole size={16} />
                  <input
                    id="password"
                    type="password"
                    autoComplete="current-password"
                    value={password}
                    placeholder="Tu contrasena"
                    onChange={(event) => setPassword(event.target.value)}
                    required
                  />
                </div>
              </FormField>

              {error ? <div className="form-alert">{error}</div> : null}

              <Button
                type="submit"
                fullWidth
                size="lg"
                loading={submitting}
                icon={<ArrowRight size={18} />}
              >
                Entrar al panel
              </Button>
            </form>

            <p className="login-card__note">
              Las credenciales iniciales se configuran en el archivo <code>application.yml</code>{' '}
              del backend.
            </p>
          </div>
        </section>
      </div>
    </div>
  );
}
