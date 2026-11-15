import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  ArrowRight,
  CalendarDays,
  CheckCircle2,
  Clock3,
  RefreshCw,
  Wallet,
} from 'lucide-react';
import { adminApi } from '../api/adminApi';
import type { Appointment, AppointmentStatus, DashboardStats } from '../types';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { normalizeError } from '../utils/errors';
import { formatLongDate, todayISO } from '../utils/date';
import { formatMoney } from '../utils/money';
import { Spinner } from '../components/ui/Spinner';
import { EmptyState } from '../components/ui/EmptyState';
import { Button } from '../components/ui/Button';
import { AppointmentTable } from '../components/admin/AppointmentTable';
import { ConfirmDialog } from '../components/admin/ConfirmDialog';

const CONFIRM_COPY: Record<string, { title: string; message: string; label: string }> = {
  CONFIRMED: {
    title: 'Confirmar cita',
    message: 'La cita pasara a estado confirmada y el cliente quedara agendado.',
    label: 'Confirmar',
  },
  COMPLETED: {
    title: 'Marcar como completada',
    message: 'Confirma que el servicio ya fue realizado para esta cita.',
    label: 'Completar',
  },
  CANCELLED: {
    title: 'Cancelar cita',
    message: 'La cita se cancelara y el horario quedara disponible nuevamente.',
    label: 'Si, cancelar',
  },
};

export function AdminDashboardPage() {
  const { session } = useAuth();
  const { notify } = useToast();
  const today = todayISO();

  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [refreshKey, setRefreshKey] = useState(0);

  const [change, setChange] = useState<{ appointment: Appointment; status: AppointmentStatus } | null>(
    null,
  );
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [statsData, appointmentsData] = await Promise.all([
        adminApi.dashboard(today),
        adminApi.listAppointments(today),
      ]);
      setStats(statsData);
      setAppointments(appointmentsData);
    } catch (caught) {
      setError(normalizeError(caught).message);
    } finally {
      setLoading(false);
    }
  }, [today]);

  useEffect(() => {
    void load();
  }, [load, refreshKey]);

  const confirmChange = async () => {
    if (!change) {
      return;
    }
    setBusy(true);
    try {
      const updated = await adminApi.changeStatus(change.appointment.id, change.status);
      setAppointments((current) =>
        current.map((appointment) => (appointment.id === updated.id ? updated : appointment)),
      );
      setStats(await adminApi.dashboard(today));
      notify(`Cita actualizada: ${updated.statusLabel}`, 'success');
      setChange(null);
    } catch (caught) {
      notify(normalizeError(caught).message, 'error');
    } finally {
      setBusy(false);
    }
  };

  const greeting =
    new Date().getHours() < 12
      ? 'Buenos dias'
      : new Date().getHours() < 19
        ? 'Buenas tardes'
        : 'Buenas noches';

  const copy = change ? CONFIRM_COPY[change.status] : null;

  return (
    <div className="admin-page">
      <header className="admin-page__header">
        <div>
          <span className="eyebrow">Panel administrativo</span>
          <h1>
            {greeting}, {session?.name?.split(' ')[0] ?? 'barbero'}
          </h1>
          <p>Este es el resumen de tu agenda para {formatLongDate(today)}.</p>
        </div>
        <Button
          variant="secondary"
          icon={<RefreshCw size={16} />}
          onClick={() => setRefreshKey((key) => key + 1)}
          loading={loading}
        >
          Actualizar
        </Button>
      </header>

      {loading && !stats ? (
        <Spinner label="Cargando panel..." />
      ) : error ? (
        <EmptyState
          icon={<CalendarDays size={26} />}
          title="No pudimos cargar el panel"
          description={error}
          action={
            <Button variant="secondary" onClick={() => setRefreshKey((key) => key + 1)}>
              Reintentar
            </Button>
          }
        />
      ) : (
        <>
          <div className="stat-grid">
            <article className="stat-card">
              <span className="stat-card__icon stat-card__icon--gold">
                <CalendarDays size={20} />
              </span>
              <div>
                <span>Citas de hoy</span>
                <strong>{stats?.todayAppointments ?? 0}</strong>
              </div>
            </article>
            <article className="stat-card">
              <span className="stat-card__icon stat-card__icon--amber">
                <Clock3 size={20} />
              </span>
              <div>
                <span>Pendientes</span>
                <strong>{stats?.pending ?? 0}</strong>
              </div>
            </article>
            <article className="stat-card">
              <span className="stat-card__icon stat-card__icon--green">
                <CheckCircle2 size={20} />
              </span>
              <div>
                <span>Confirmadas</span>
                <strong>{stats?.confirmed ?? 0}</strong>
              </div>
            </article>
            <article className="stat-card">
              <span className="stat-card__icon stat-card__icon--ink">
                <Wallet size={20} />
              </span>
              <div>
                <span>Ingresos estimados</span>
                <strong>{formatMoney(stats?.estimatedRevenue ?? 0)}</strong>
              </div>
            </article>
          </div>

          <div className="dashboard-note">
            <span>
              {stats?.completed ?? 0} completadas · {stats?.cancelled ?? 0} canceladas hoy
            </span>
            <Link to="/admin/citas" className="text-link">
              Ver todas las citas
              <ArrowRight size={15} />
            </Link>
          </div>

          <section className="panel panel--flush">
            <div className="panel__heading panel__heading--row">
              <div>
                <h2>Agenda de hoy</h2>
                <p>{formatLongDate(today)}</p>
              </div>
              <span className="pill">{appointments.length} citas</span>
            </div>

            {appointments.length === 0 ? (
              <EmptyState
                icon={<CalendarDays size={26} />}
                title="No hay citas para hoy"
                description="Cuando un cliente reserve, la cita aparecera aqui automaticamente."
                action={
                  <Link to="/reservar" target="_blank">
                    <Button variant="secondary">Abrir pagina de reservas</Button>
                  </Link>
                }
              />
            ) : (
              <AppointmentTable
                appointments={appointments}
                busyId={busy ? change?.appointment.id : null}
                onChangeStatus={(appointment, status) => setChange({ appointment, status })}
              />
            )}
          </section>
        </>
      )}

      <ConfirmDialog
        open={Boolean(change)}
        title={copy?.title ?? ''}
        message={copy?.message ?? ''}
        confirmLabel={copy?.label}
        tone={change?.status === 'CANCELLED' ? 'danger' : 'primary'}
        loading={busy}
        onConfirm={() => void confirmChange()}
        onClose={() => setChange(null)}
      />
    </div>
  );
}
