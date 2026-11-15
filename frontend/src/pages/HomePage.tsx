import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  ArrowRight,
  CalendarCheck,
  Clock3,
  Scissors,
  Sparkles,
  Star,
  UserRound,
} from 'lucide-react';
import { publicApi } from '../api/publicApi';
import type { Service } from '../types';
import { formatDuration, formatMoney } from '../utils/money';
import { Spinner } from '../components/ui/Spinner';
import { EmptyState } from '../components/ui/EmptyState';
import { Button } from '../components/ui/Button';

export function HomePage() {
  const [services, setServices] = useState<Service[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(null);
    publicApi
      .listServices()
      .then((data) => {
        if (active) {
          setServices(data);
        }
      })
      .catch(() => {
        if (active) {
          setError('No pudimos cargar los servicios. Verifica tu conexion.');
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });
    return () => {
      active = false;
    };
  }, [reloadKey]);

  return (
    <div className="home">
      <section className="hero">
        <div className="container hero__grid">
          <div className="hero__content">
            <span className="eyebrow">
              <Sparkles size={15} />
              Barberia artesanal · Agenda en linea
            </span>
            <h1>
              Tu estilo,
              <br />
              <em>en su mejor momento.</em>
            </h1>
            <p className="hero__lead">
              Reserva tu cita en menos de un minuto. Elige el servicio, el dia y la hora que mejor
              te convengan. Sin llamadas, sin esperas, sin complicaciones.
            </p>
            <div className="hero__actions">
              <Link to="/reservar">
                <Button size="lg" icon={<CalendarCheck size={19} />}>
                  Reservar mi cita
                </Button>
              </Link>
              <a href="#servicios">
                <Button size="lg" variant="outline" icon={<Scissors size={18} />}>
                  Ver servicios
                </Button>
              </a>
            </div>
            <div className="hero__metrics">
              <div>
                <strong>+8</strong>
                <span>anos de oficio</span>
              </div>
              <div>
                <strong>1</strong>
                <span>barbero experto</span>
              </div>
              <div>
                <strong>100%</strong>
                <span>atencion personalizada</span>
              </div>
            </div>
          </div>

          <div className="hero__visual" aria-hidden="true">
            <div className="hero__frame">
              <div className="hero__pole" />
              <div className="hero__card">
                <Star size={22} />
                <p>"Cada corte es una conversacion, cada cliente un estilo distinto."</p>
                <span>— Martin, barbero</span>
              </div>
              <div className="hero__badge">
                <Clock3 size={18} />
                <div>
                  <strong>Abierto hoy</strong>
                  <span>Reserva disponible</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="section" id="servicios">
        <div className="container">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Nuestro catalogo</span>
              <h2>Servicios pensados para ti</h2>
            </div>
            <p>
              Precios claros, duracion real y un espacio reservado solo para tu cita. Elige el
              servicio y nosotros nos encargamos del resto.
            </p>
          </div>

          {loading ? (
            <Spinner label="Cargando servicios..." />
          ) : error ? (
            <EmptyState
              icon={<Scissors size={26} />}
              title="No pudimos mostrar los servicios"
              description={error}
              action={
                <Button variant="secondary" onClick={() => setReloadKey((key) => key + 1)}>
                  Reintentar
                </Button>
              }
            />
          ) : services.length === 0 ? (
            <EmptyState
              icon={<Scissors size={26} />}
              title="Aun no hay servicios publicados"
              description="Muy pronto tendremos nuevos servicios disponibles."
            />
          ) : (
            <div className="service-grid">
              {services.map((service, index) => (
                <article className="service-card" key={service.id}>
                  <div className="service-card__top">
                    <span className="service-card__index">{`${index + 1}`.padStart(2, '0')}</span>
                    <span className="service-card__duration">
                      <Clock3 size={14} />
                      {formatDuration(service.durationMinutes)}
                    </span>
                  </div>
                  <h3>{service.name}</h3>
                  <p>{service.description ?? 'Servicio profesional con atencion personalizada.'}</p>
                  <div className="service-card__footer">
                    <strong>{formatMoney(service.price)}</strong>
                    <Link to={`/reservar?service=${service.id}`} className="service-card__link">
                      Reservar
                      <ArrowRight size={16} />
                    </Link>
                  </div>
                </article>
              ))}
            </div>
          )}
        </div>
      </section>

      <section className="section section--muted" id="como-funciona">
        <div className="container">
          <div className="section-heading section-heading--center">
            <span className="eyebrow">Asi de facil</span>
            <h2>Reserva en tres pasos</h2>
            <p>Disenamos el proceso para que solo te preocupes por llegar y verte bien.</p>
          </div>
          <div className="steps-grid">
            <article className="step-card">
              <span className="step-card__number">01</span>
              <div className="step-card__icon">
                <Scissors size={22} />
              </div>
              <h3>Elige tu servicio</h3>
              <p>Corte clasico, corte con barba, perfilado o afeitado tradicional.</p>
            </article>
            <article className="step-card">
              <span className="step-card__number">02</span>
              <div className="step-card__icon">
                <CalendarCheck size={22} />
              </div>
              <h3>Selecciona fecha y hora</h3>
              <p>Consulta en tiempo real los espacios disponibles y escoge el ideal.</p>
            </article>
            <article className="step-card">
              <span className="step-card__number">03</span>
              <div className="step-card__icon">
                <UserRound size={22} />
              </div>
              <h3>Confirma tus datos</h3>
              <p>Sin registros ni contrasenas. Recibes tu comprobante al instante.</p>
            </article>
          </div>
        </div>
      </section>

      <section className="section">
        <div className="container">
          <div className="cta-band">
            <div>
              <span className="eyebrow eyebrow--light">Tu proxima cita empieza aqui</span>
              <h2>Reserva hoy y llega directo a la silla.</h2>
              <p>Horarios disponibles de lunes a sabado. Confirmacion inmediata.</p>
            </div>
            <Link to="/reservar">
              <Button size="lg" variant="primary" icon={<ArrowRight size={18} />}>
                Agendar ahora
              </Button>
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
