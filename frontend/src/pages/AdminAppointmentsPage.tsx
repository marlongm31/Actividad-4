import { useCallback, useEffect, useState } from 'react';
import { CalendarDays, Filter, RefreshCw } from 'lucide-react';
import { adminApi } from '../api/adminApi';
import type { Appointment, AppointmentStatus } from '../types';
import { useToast } from '../context/ToastContext';
import { normalizeError } from '../utils/errors';
import { formatLongDate, todayISO } from '../utils/date';
import { statusMeta } from '../utils/status';
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

export function AdminAppointmentsPage() {
  const { notify } = useToast();
  const [date, setDate] = useState(todayISO());
  const [status, setStatus] = useState<AppointmentStatus | ''>('');
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
      const data = await adminApi.listAppointments(date || undefined, status || undefined);
      setAppointments(data);
    } catch (caught) {
      setError(normalizeError(caught).message);
    } finally {
      setLoading(false);
    }
  }, [date, status]);

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
      notify(`Cita actualizada: ${updated.statusLabel}`, 'success');
      setChange(null);
      if (status) {
        setRefreshKey((key) => key + 1);
      }
    } catch (caught) {
      notify(normalizeError(caught).message, 'error');
    } finally {
      setBusy(false);
    }
  };

  const copy = change ? CONFIRM_COPY[change.status] : null;
  const filterLabel = date ? formatLongDate(date) : 'Todas las fechas';
  const statusLabel = status ? statusMeta(status).label : 'Todos los estados';

  return (
    <div className="admin-page">
      <header className="admin-page__header">
        <div>
          <span className="eyebrow">Gestion</span>
          <h1>Citas</h1>
          <p>Consulta, confirma, completa o cancela las reservas de tus clientes.</p>
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

      <section className="filter-bar">
        <div className="filter-bar__group">
          <label htmlFor="filter-date">Fecha</label>
          <input
            id="filter-date"
            type="date"
            value={date}
            onChange={(event) => setDate(event.target.value)}
          />
        </div>
        <div className="filter-bar__group">
          <label htmlFor="filter-status">Estado</label>
          <select
            id="filter-status"
            value={status}
            onChange={(event) => setStatus(event.target.value as AppointmentStatus | '')}
          >
            <option value="">Todos los estados</option>
            <option value="PENDING">Pendientes</option>
            <option value="CONFIRMED">Confirmadas</option>
            <option value="COMPLETED">Completadas</option>
            <option value="CANCELLED">Canceladas</option>
          </select>
        </div>
        <div className="filter-bar__actions">
          <Button variant="ghost" onClick={() => setDate(todayISO())}>
            Hoy
          </Button>
          <Button variant="ghost" onClick={() => setDate('')}>
            Todas las fechas
          </Button>
        </div>
      </section>

      <div className="list-meta">
        <span className="pill">
          <Filter size={13} />
          {filterLabel} · {statusLabel}
        </span>
        <span>{appointments.length} citas encontradas</span>
      </div>

      {loading ? (
        <Spinner label="Cargando citas..." />
      ) : error ? (
        <EmptyState
          icon={<CalendarDays size={26} />}
          title="No pudimos cargar las citas"
          description={error}
          action={
            <Button variant="secondary" onClick={() => setRefreshKey((key) => key + 1)}>
              Reintentar
            </Button>
          }
        />
      ) : appointments.length === 0 ? (
        <EmptyState
          icon={<CalendarDays size={26} />}
          title="No hay citas con estos filtros"
          description="Prueba con otra fecha, otro estado o consulta todas las fechas."
          action={
            <Button
              variant="secondary"
              onClick={() => {
                setDate('');
                setStatus('');
              }}
            >
              Limpiar filtros
            </Button>
          }
        />
      ) : (
        <section className="panel panel--flush">
          <AppointmentTable
            appointments={appointments}
            busyId={busy ? change?.appointment.id : null}
            onChangeStatus={(appointment, nextStatus) =>
              setChange({ appointment, status: nextStatus })
            }
          />
        </section>
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
