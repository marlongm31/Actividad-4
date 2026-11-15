import { useEffect, useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import {
  ArrowLeft,
  ArrowRight,
  CalendarDays,
  Check,
  CheckCircle2,
  Clock3,
  Copy,
  Printer,
  Scissors,
  UserRound,
} from 'lucide-react';
import { publicApi } from '../api/publicApi';
import type { Appointment, Service, TimeSlot } from '../types';
import { normalizeError } from '../utils/errors';
import { formatDuration, formatMoney } from '../utils/money';
import {
  formatLongDate,
  formatMonthShort,
  formatTime,
  formatTimeRange,
  dayNumber,
  nextDays,
  relativeDayLabel,
  todayISO,
} from '../utils/date';
import { useToast } from '../context/ToastContext';
import { Button } from '../components/ui/Button';
import { FormField } from '../components/ui/FormField';
import { Spinner } from '../components/ui/Spinner';
import { EmptyState } from '../components/ui/EmptyState';

const STEPS = ['Servicio', 'Fecha y hora', 'Tus datos'];
const DATE_OPTIONS = nextDays(21);

interface BookingForm {
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  notes: string;
}

const EMPTY_FORM: BookingForm = {
  customerName: '',
  customerPhone: '',
  customerEmail: '',
  notes: '',
};

export function BookingPage() {
  const [searchParams] = useSearchParams();
  const { notify } = useToast();
  const requestedServiceId = Number(searchParams.get('service'));

  const [services, setServices] = useState<Service[]>([]);
  const [loadingServices, setLoadingServices] = useState(true);
  const [servicesError, setServicesError] = useState<string | null>(null);
  const [selectedService, setSelectedService] = useState<Service | null>(null);
  const [step, setStep] = useState(1);

  const [selectedDate, setSelectedDate] = useState(todayISO());
  const [slots, setSlots] = useState<TimeSlot[]>([]);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [slotsError, setSlotsError] = useState<string | null>(null);
  const [selectedSlot, setSelectedSlot] = useState<TimeSlot | null>(null);

  const [form, setForm] = useState<BookingForm>(EMPTY_FORM);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [submitting, setSubmitting] = useState(false);
  const [appointment, setAppointment] = useState<Appointment | null>(null);

  useEffect(() => {
    let active = true;
    publicApi
      .listServices()
      .then((data) => {
        if (!active) {
          return;
        }
        setServices(data);
        if (Number.isFinite(requestedServiceId) && requestedServiceId > 0) {
          const match = data.find((service) => service.id === requestedServiceId);
          if (match) {
            setSelectedService(match);
          }
        }
      })
      .catch((error) => {
        if (active) {
          setServicesError(normalizeError(error).message);
        }
      })
      .finally(() => {
        if (active) {
          setLoadingServices(false);
        }
      });
    return () => {
      active = false;
    };
  }, [requestedServiceId]);

  useEffect(() => {
    if (step < 2 || !selectedService) {
      return;
    }
    let active = true;
    setLoadingSlots(true);
    setSlotsError(null);
    setSelectedSlot(null);
    publicApi
      .getAvailability(selectedDate, selectedService.id)
      .then((data) => {
        if (active) {
          setSlots(data.slots);
        }
      })
      .catch((error) => {
        if (active) {
          setSlots([]);
          setSlotsError(normalizeError(error).message);
        }
      })
      .finally(() => {
        if (active) {
          setLoadingSlots(false);
        }
      });
    return () => {
      active = false;
    };
  }, [step, selectedDate, selectedService]);

  const summaryDate = useMemo(
    () => formatLongDate(selectedDate),
    [selectedDate],
  );

  const goTo = (nextStep: number) => {
    setStep(nextStep);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const selectService = (service: Service) => {
    setSelectedService(service);
    setSelectedSlot(null);
  };

  const continueFromService = () => {
    if (!selectedService) {
      notify('Selecciona un servicio para continuar', 'info');
      return;
    }
    goTo(2);
  };

  const continueFromDateTime = () => {
    if (!selectedSlot) {
      notify('Selecciona un horario disponible', 'info');
      return;
    }
    goTo(3);
  };

  const validateForm = (): boolean => {
    const errors: Record<string, string> = {};
    if (!form.customerName.trim()) {
      errors.customerName = 'Escribe tu nombre completo';
    }
    if (!form.customerPhone.trim()) {
      errors.customerPhone = 'Escribe tu numero de telefono';
    } else if (!/^[0-9+()\-\s]{7,30}$/.test(form.customerPhone.trim())) {
      errors.customerPhone = 'El telefono tiene un formato invalido';
    }
    if (form.customerEmail.trim() && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.customerEmail.trim())) {
      errors.customerEmail = 'Escribe un correo valido';
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const submit = async () => {
    if (!selectedService || !selectedSlot || !validateForm()) {
      return;
    }
    setSubmitting(true);
    try {
      const created = await publicApi.bookAppointment({
        serviceId: selectedService.id,
        date: selectedDate,
        startTime: selectedSlot.startTime,
        customerName: form.customerName.trim(),
        customerPhone: form.customerPhone.trim(),
        customerEmail: form.customerEmail.trim() || undefined,
        notes: form.notes.trim() || undefined,
      });
      setAppointment(created);
      setStep(4);
      notify('Tu cita fue reservada con exito', 'success');
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (error) {
      const normalized = normalizeError(error);
      setFieldErrors(normalized.fieldErrors);
      notify(normalized.message, 'error');
      if (Object.keys(normalized.fieldErrors).length === 0) {
        setStep(2);
        setSelectedSlot(null);
      }
    } finally {
      setSubmitting(false);
    }
  };

  const startOver = () => {
    setAppointment(null);
    setSelectedSlot(null);
    setForm(EMPTY_FORM);
    setFieldErrors({});
    setStep(1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const copyCode = async () => {
    if (!appointment) {
      return;
    }
    const code = `BT-${String(appointment.id).padStart(4, '0')}`;
    try {
      await navigator.clipboard.writeText(code);
      notify('Codigo de reserva copiado', 'success');
    } catch {
      notify(`Tu codigo de reserva es ${code}`, 'info');
    }
  };

  if (step === 4 && appointment) {
    return (
      <div className="booking-page">
        <div className="container">
          <section className="success-card">
            <div className="success-card__icon">
              <CheckCircle2 size={38} />
            </div>
            <span className="eyebrow">Reserva confirmada</span>
            <h1>¡Listo! Tu cita esta agendada.</h1>
            <p>
              Te esperamos el <strong>{formatLongDate(appointment.date)}</strong> a las{' '}
              <strong>{formatTime(appointment.startTime)}</strong>. Llega 10 minutos antes para
              empezar a tiempo.
            </p>
            <div className="success-card__code">
              <span>Codigo de reserva</span>
              <strong>BT-{String(appointment.id).padStart(4, '0')}</strong>
              <button type="button" onClick={copyCode} aria-label="Copiar codigo">
                <Copy size={15} />
              </button>
            </div>
            <div className="success-card__details">
              <div>
                <span>Servicio</span>
                <strong>{appointment.serviceName}</strong>
              </div>
              <div>
                <span>Fecha</span>
                <strong>{formatLongDate(appointment.date)}</strong>
              </div>
              <div>
                <span>Hora</span>
                <strong>{formatTimeRange(appointment.startTime, appointment.endTime)}</strong>
              </div>
              <div>
                <span>Valor</span>
                <strong>{formatMoney(appointment.servicePrice)}</strong>
              </div>
              <div>
                <span>Cliente</span>
                <strong>{appointment.customerName}</strong>
              </div>
              <div>
                <span>Estado</span>
                <strong>{appointment.statusLabel}</strong>
              </div>
            </div>
            <div className="success-card__actions">
              <Button variant="secondary" icon={<Printer size={17} />} onClick={() => window.print()}>
                Imprimir comprobante
              </Button>
              <Button variant="primary" icon={<CalendarDays size={17} />} onClick={startOver}>
                Reservar otra cita
              </Button>
              <Link to="/">
                <Button variant="ghost">Volver al inicio</Button>
              </Link>
            </div>
          </section>
        </div>
      </div>
    );
  }

  return (
    <div className="booking-page">
      <header className="booking-hero">
        <div className="container">
          <span className="eyebrow eyebrow--light">Agenda en linea</span>
          <h1>Reserva tu cita</h1>
          <p>Elige el servicio, el momento y confirma tus datos. Nosotros ponemos el estilo.</p>
        </div>
      </header>

      <div className="container booking-shell">
        <div className="booking-steps" aria-label="Progreso de la reserva">
          {STEPS.map((label, index) => {
            const number = index + 1;
            const state = number < step ? 'is-done' : number === step ? 'is-active' : '';
            return (
              <div key={label} className={`booking-step ${state}`}>
                <span className="booking-step__number">
                  {number < step ? <Check size={14} /> : number}
                </span>
                <span className="booking-step__label">{label}</span>
              </div>
            );
          })}
        </div>

        <div className="booking-layout">
          <div className="booking-main">
            {step === 1 ? (
              <section className="panel">
                <div className="panel__heading">
                  <span className="panel__step">Paso 1</span>
                  <h2>Elige tu servicio</h2>
                  <p>Selecciona lo que necesitas y revisa la duracion y el precio.</p>
                </div>

                {loadingServices ? (
                  <Spinner label="Cargando servicios..." />
                ) : servicesError ? (
                  <EmptyState
                    icon={<Scissors size={26} />}
                    title="No pudimos cargar los servicios"
                    description={servicesError}
                  />
                ) : (
                  <div className="service-options">
                    {services.map((service) => {
                      const active = selectedService?.id === service.id;
                      return (
                        <button
                          type="button"
                          key={service.id}
                          className={`service-option ${active ? 'is-selected' : ''}`}
                          onClick={() => selectService(service)}
                          aria-pressed={active}
                        >
                          <span className="service-option__check">
                            {active ? <Check size={14} /> : null}
                          </span>
                          <span className="service-option__body">
                            <span className="service-option__name">{service.name}</span>
                            <span className="service-option__desc">
                              {service.description ?? 'Atencion personalizada'}
                            </span>
                            <span className="service-option__meta">
                              <Clock3 size={14} />
                              {formatDuration(service.durationMinutes)}
                            </span>
                          </span>
                          <span className="service-option__price">{formatMoney(service.price)}</span>
                        </button>
                      );
                    })}
                  </div>
                )}

                <div className="panel__actions">
                  <Link to="/">
                    <Button variant="ghost" icon={<ArrowLeft size={17} />}>
                      Volver
                    </Button>
                  </Link>
                  <Button
                    icon={<ArrowRight size={17} />}
                    onClick={continueFromService}
                    disabled={!selectedService}
                  >
                    Elegir fecha y hora
                  </Button>
                </div>
              </section>
            ) : null}

            {step === 2 ? (
              <section className="panel">
                <div className="panel__heading">
                  <span className="panel__step">Paso 2</span>
                  <h2>Elige fecha y hora</h2>
                  <p>Los horarios se actualizan en tiempo real segun la agenda del barbero.</p>
                </div>

                <div className="date-strip" role="list" aria-label="Fechas disponibles">
                  {DATE_OPTIONS.map((date) => {
                    const active = date === selectedDate;
                    return (
                      <button
                        type="button"
                        key={date}
                        role="listitem"
                        className={`date-chip ${active ? 'is-selected' : ''}`}
                        onClick={() => setSelectedDate(date)}
                        aria-pressed={active}
                      >
                        <span>{relativeDayLabel(date)}</span>
                        <strong>{dayNumber(date)}</strong>
                        <small>{formatMonthShort(date)}</small>
                      </button>
                    );
                  })}
                </div>

                <div className="slots-header">
                  <h3>
                    {summaryDate}
                    <span>{selectedService ? ` · ${selectedService.name}` : ''}</span>
                  </h3>
                  {selectedService ? (
                    <span className="slots-header__duration">
                      <Clock3 size={14} />
                      {formatDuration(selectedService.durationMinutes)}
                    </span>
                  ) : null}
                </div>

                {loadingSlots ? (
                  <Spinner label="Consultando disponibilidad..." />
                ) : slotsError ? (
                  <EmptyState
                    icon={<CalendarDays size={26} />}
                    title="No pudimos consultar la disponibilidad"
                    description={slotsError}
                  />
                ) : slots.length === 0 ? (
                  <EmptyState
                    icon={<Clock3 size={26} />}
                    title="No hay horarios disponibles"
                    description="Puede que el dia este cerrado o que la agenda ya este completa. Prueba con otra fecha."
                  />
                ) : (
                  <div className="slot-grid">
                    {slots.map((slot) => {
                      const active = selectedSlot?.startTime === slot.startTime;
                      return (
                        <button
                          type="button"
                          key={slot.startTime}
                          className={`slot ${active ? 'is-selected' : ''}`}
                          onClick={() => setSelectedSlot(slot)}
                          aria-pressed={active}
                        >
                          {formatTime(slot.startTime)}
                        </button>
                      );
                    })}
                  </div>
                )}

                <div className="panel__actions">
                  <Button variant="ghost" icon={<ArrowLeft size={17} />} onClick={() => goTo(1)}>
                    Atras
                  </Button>
                  <Button
                    icon={<ArrowRight size={17} />}
                    onClick={continueFromDateTime}
                    disabled={!selectedSlot}
                  >
                    Continuar
                  </Button>
                </div>
              </section>
            ) : null}

            {step === 3 ? (
              <section className="panel">
                <div className="panel__heading">
                  <span className="panel__step">Paso 3</span>
                  <h2>Confirma tus datos</h2>
                  <p>No necesitas crear una cuenta. Solo dejanos tus datos de contacto.</p>
                </div>

                <form
                  className="booking-form"
                  onSubmit={(event) => {
                    event.preventDefault();
                    void submit();
                  }}
                  noValidate
                >
                  <FormField
                    label="Nombre completo"
                    htmlFor="customerName"
                    required
                    error={fieldErrors.customerName}
                  >
                    <input
                      id="customerName"
                      type="text"
                      value={form.customerName}
                      maxLength={120}
                      placeholder="Ej. Juan Perez"
                      onChange={(event) =>
                        setForm({ ...form, customerName: event.target.value })
                      }
                    />
                  </FormField>

                  <FormField
                    label="Telefono"
                    htmlFor="customerPhone"
                    required
                    error={fieldErrors.customerPhone}
                    hint="Te contactaremos solo si necesitamos ajustar la cita."
                  >
                    <input
                      id="customerPhone"
                      type="tel"
                      value={form.customerPhone}
                      maxLength={30}
                      placeholder="Ej. 300 123 4567"
                      onChange={(event) =>
                        setForm({ ...form, customerPhone: event.target.value })
                      }
                    />
                  </FormField>

                  <FormField
                    label="Correo electronico"
                    htmlFor="customerEmail"
                    error={fieldErrors.customerEmail}
                    hint="Opcional, para enviarte recordatorios."
                  >
                    <input
                      id="customerEmail"
                      type="email"
                      value={form.customerEmail}
                      maxLength={150}
                      placeholder="Ej. juan@correo.com"
                      onChange={(event) =>
                        setForm({ ...form, customerEmail: event.target.value })
                      }
                    />
                  </FormField>

                  <FormField
                    label="Notas para el barbero"
                    htmlFor="notes"
                    error={fieldErrors.notes}
                    hint="Opcional. Preferencias, referencias o alergias."
                  >
                    <textarea
                      id="notes"
                      rows={3}
                      value={form.notes}
                      maxLength={500}
                      placeholder="Ej. Maquina numero 2 a los lados"
                      onChange={(event) => setForm({ ...form, notes: event.target.value })}
                    />
                  </FormField>

                  <div className="panel__actions panel__actions--form">
                    <Button
                      type="button"
                      variant="ghost"
                      icon={<ArrowLeft size={17} />}
                      onClick={() => goTo(2)}
                    >
                      Atras
                    </Button>
                    <Button
                      type="submit"
                      icon={<Check size={17} />}
                      loading={submitting}
                    >
                      Confirmar reserva
                    </Button>
                  </div>
                </form>
              </section>
            ) : null}
          </div>

          <aside className="booking-summary">
            <span className="eyebrow">Resumen</span>
            <h2>Tu cita</h2>

            <div className="summary-service">
              <div className="summary-service__icon">
                <Scissors size={20} />
              </div>
              <div>
                <strong>{selectedService?.name ?? 'Selecciona un servicio'}</strong>
                <span>
                  {selectedService
                    ? formatDuration(selectedService.durationMinutes)
                    : 'Aun sin seleccionar'}
                </span>
              </div>
            </div>

            <dl className="summary-list">
              <div>
                <dt>
                  <CalendarDays size={15} /> Fecha
                </dt>
                <dd>{selectedService ? summaryDate : 'Por definir'}</dd>
              </div>
              <div>
                <dt>
                  <Clock3 size={15} /> Hora
                </dt>
                <dd>
                  {selectedSlot
                    ? formatTimeRange(selectedSlot.startTime, selectedSlot.endTime)
                    : 'Por definir'}
                </dd>
              </div>
              <div>
                <dt>
                  <UserRound size={15} /> Cliente
                </dt>
                <dd>{form.customerName.trim() || 'Por completar'}</dd>
              </div>
            </dl>

            <div className="summary-total">
              <span>Total a pagar en el local</span>
              <strong>{selectedService ? formatMoney(selectedService.price) : '—'}</strong>
            </div>

            <p className="summary-note">
              La reserva queda pendiente hasta que el barbero la confirme. Si necesitas cancelar,
              comunicate con la peluqueria.
            </p>
          </aside>
        </div>
      </div>
    </div>
  );
}
