import { useCallback, useEffect, useState } from 'react';
import { Clock3, Pencil, Plus, RefreshCw, Scissors, Trash2 } from 'lucide-react';
import { adminApi } from '../api/adminApi';
import type { Service } from '../types';
import { useToast } from '../context/ToastContext';
import { normalizeError } from '../utils/errors';
import { formatDuration, formatMoney } from '../utils/money';
import { Spinner } from '../components/ui/Spinner';
import { EmptyState } from '../components/ui/EmptyState';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { ServiceFormModal } from '../components/admin/ServiceFormModal';
import { ConfirmDialog } from '../components/admin/ConfirmDialog';

export function AdminServicesPage() {
  const { notify } = useToast();
  const [services, setServices] = useState<Service[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [refreshKey, setRefreshKey] = useState(0);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Service | null>(null);
  const [deleting, setDeleting] = useState<Service | null>(null);
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setServices(await adminApi.listServices());
    } catch (caught) {
      setError(normalizeError(caught).message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load, refreshKey]);

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const openEdit = (service: Service) => {
    setEditing(service);
    setFormOpen(true);
  };

  const deactivate = async () => {
    if (!deleting) {
      return;
    }
    setBusy(true);
    try {
      await adminApi.deactivateService(deleting.id);
      notify('Servicio desactivado', 'success');
      setDeleting(null);
      setRefreshKey((key) => key + 1);
    } catch (caught) {
      notify(normalizeError(caught).message, 'error');
    } finally {
      setBusy(false);
    }
  };

  const reactivate = async (service: Service) => {
    try {
      await adminApi.updateService(service.id, {
        name: service.name,
        description: service.description ?? '',
        price: service.price,
        durationMinutes: service.durationMinutes,
        active: true,
      });
      notify('Servicio reactivado', 'success');
      setRefreshKey((key) => key + 1);
    } catch (caught) {
      notify(normalizeError(caught).message, 'error');
    }
  };

  const activeCount = services.filter((service) => service.active).length;

  return (
    <div className="admin-page">
      <header className="admin-page__header">
        <div>
          <span className="eyebrow">Catalogo</span>
          <h1>Servicios</h1>
          <p>Administra los servicios, precios y duraciones que ofreces a tus clientes.</p>
        </div>
        <div className="admin-page__actions">
          <Button
            variant="secondary"
            icon={<RefreshCw size={16} />}
            onClick={() => setRefreshKey((key) => key + 1)}
            loading={loading}
          >
            Actualizar
          </Button>
          <Button icon={<Plus size={17} />} onClick={openCreate}>
            Nuevo servicio
          </Button>
        </div>
      </header>

      <div className="list-meta">
        <span className="pill">
          {services.length} servicios · {activeCount} activos
        </span>
      </div>

      {loading ? (
        <Spinner label="Cargando servicios..." />
      ) : error ? (
        <EmptyState
          icon={<Scissors size={26} />}
          title="No pudimos cargar los servicios"
          description={error}
          action={
            <Button variant="secondary" onClick={() => setRefreshKey((key) => key + 1)}>
              Reintentar
            </Button>
          }
        />
      ) : services.length === 0 ? (
        <EmptyState
          icon={<Scissors size={26} />}
          title="Aun no tienes servicios"
          description="Crea tu primer servicio para que los clientes puedan reservar."
          action={
            <Button icon={<Plus size={17} />} onClick={openCreate}>
              Crear servicio
            </Button>
          }
        />
      ) : (
        <div className="admin-service-grid">
          {services.map((service) => (
            <article
              key={service.id}
              className={`admin-service-card ${service.active ? '' : 'is-inactive'}`}
            >
              <div className="admin-service-card__top">
                <span className="admin-service-card__icon">
                  <Scissors size={19} />
                </span>
                <Badge tone={service.active ? 'success' : 'neutral'}>
                  {service.active ? 'Activo' : 'Inactivo'}
                </Badge>
              </div>
              <h3>{service.name}</h3>
              <p>{service.description ?? 'Sin descripcion registrada.'}</p>
              <div className="admin-service-card__meta">
                <span>
                  <Clock3 size={14} />
                  {formatDuration(service.durationMinutes)}
                </span>
                <strong>{formatMoney(service.price)}</strong>
              </div>
              <div className="admin-service-card__actions">
                <Button
                  size="sm"
                  variant="secondary"
                  icon={<Pencil size={14} />}
                  onClick={() => openEdit(service)}
                >
                  Editar
                </Button>
                {service.active ? (
                  <Button
                    size="sm"
                    variant="ghost"
                    icon={<Trash2 size={14} />}
                    onClick={() => setDeleting(service)}
                  >
                    Desactivar
                  </Button>
                ) : (
                  <Button size="sm" variant="ghost" onClick={() => void reactivate(service)}>
                    Reactivar
                  </Button>
                )}
              </div>
            </article>
          ))}
        </div>
      )}

      <ServiceFormModal
        open={formOpen}
        service={editing}
        onClose={() => setFormOpen(false)}
        onSaved={() => setRefreshKey((key) => key + 1)}
      />

      <ConfirmDialog
        open={Boolean(deleting)}
        title="Desactivar servicio"
        message={`El servicio "${deleting?.name ?? ''}" dejara de mostrarse a los clientes. Las citas existentes no se modifican.`}
        confirmLabel="Desactivar"
        tone="danger"
        loading={busy}
        onConfirm={() => void deactivate()}
        onClose={() => setDeleting(null)}
      />
    </div>
  );
}
