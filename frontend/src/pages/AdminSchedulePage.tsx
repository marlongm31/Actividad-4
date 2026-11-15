import { useCallback, useEffect, useState } from 'react';
import { Clock3, RefreshCw, Save } from 'lucide-react';
import { adminApi } from '../api/adminApi';
import type { BusinessHours } from '../types';
import { useToast } from '../context/ToastContext';
import { normalizeError } from '../utils/errors';
import { Spinner } from '../components/ui/Spinner';
import { EmptyState } from '../components/ui/EmptyState';
import { Button } from '../components/ui/Button';

function timeValue(value: string): string {
  return value.slice(0, 5);
}

export function AdminSchedulePage() {
  const { notify } = useToast();
  const [days, setDays] = useState<BusinessHours[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [refreshKey, setRefreshKey] = useState(0);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setDays(await adminApi.getSchedule());
    } catch (caught) {
      setError(normalizeError(caught).message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load, refreshKey]);

  const updateDay = (dayOfWeek: number, patch: Partial<BusinessHours>) => {
    setDays((current) =>
      current.map((day) => (day.dayOfWeek === dayOfWeek ? { ...day, ...patch } : day)),
    );
  };

  const save = async () => {
    const invalid = days.find(
      (day) => day.active && timeValue(day.openTime) >= timeValue(day.closeTime),
    );
    if (invalid) {
      notify(`La hora de cierre de ${invalid.dayName} debe ser posterior a la apertura`, 'error');
      return;
    }
    setSaving(true);
    try {
      const updated = await adminApi.updateSchedule(
        days.map((day) => ({
          dayOfWeek: day.dayOfWeek,
          openTime: timeValue(day.openTime),
          closeTime: timeValue(day.closeTime),
          active: day.active,
        })),
      );
      setDays(updated);
      notify('Horarios actualizados correctamente', 'success');
    } catch (caught) {
      notify(normalizeError(caught).message, 'error');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="admin-page">
      <header className="admin-page__header">
        <div>
          <span className="eyebrow">Configuracion</span>
          <h1>Horarios de atencion</h1>
          <p>Define los dias y franjas en las que aceptas nuevas reservas.</p>
        </div>
        <div className="admin-page__actions">
          <Button
            variant="secondary"
            icon={<RefreshCw size={16} />}
            onClick={() => setRefreshKey((key) => key + 1)}
            loading={loading}
          >
            Descartar cambios
          </Button>
          <Button icon={<Save size={16} />} onClick={() => void save()} loading={saving}>
            Guardar horarios
          </Button>
        </div>
      </header>

      {loading ? (
        <Spinner label="Cargando horarios..." />
      ) : error ? (
        <EmptyState
          icon={<Clock3 size={26} />}
          title="No pudimos cargar los horarios"
          description={error}
          action={
            <Button variant="secondary" onClick={() => setRefreshKey((key) => key + 1)}>
              Reintentar
            </Button>
          }
        />
      ) : (
        <section className="panel schedule-panel">
          <div className="panel__heading">
            <h2>Disponibilidad semanal</h2>
            <p>
              Los cambios se aplican de inmediato a las nuevas reservas. Las citas existentes
              conservan su horario.
            </p>
          </div>

          <div className="schedule-list">
            {days.map((day) => (
              <div key={day.dayOfWeek} className={`schedule-row ${day.active ? '' : 'is-closed'}`}>
                <label className="switch-field switch-field--compact">
                  <input
                    type="checkbox"
                    checked={day.active}
                    onChange={(event) =>
                      updateDay(day.dayOfWeek, { active: event.target.checked })
                    }
                  />
                  <span className="switch" aria-hidden="true" />
                  <span className="schedule-row__day">
                    {day.dayName}
                    <small>{day.active ? 'Abierto' : 'Cerrado'}</small>
                  </span>
                </label>

                <div className="schedule-row__times">
                  <label>
                    <span>Apertura</span>
                    <input
                      type="time"
                      value={timeValue(day.openTime)}
                      disabled={!day.active}
                      onChange={(event) =>
                        updateDay(day.dayOfWeek, { openTime: event.target.value })
                      }
                    />
                  </label>
                  <span className="schedule-row__dash">—</span>
                  <label>
                    <span>Cierre</span>
                    <input
                      type="time"
                      value={timeValue(day.closeTime)}
                      disabled={!day.active}
                      onChange={(event) =>
                        updateDay(day.dayOfWeek, { closeTime: event.target.value })
                      }
                    />
                  </label>
                </div>
              </div>
            ))}
          </div>

          <p className="schedule-note">
            Los clientes solo pueden reservar dentro de estas franjas. El backend valida siempre
            que la cita quede completa antes de la hora de cierre.
          </p>
        </section>
      )}
    </div>
  );
}
