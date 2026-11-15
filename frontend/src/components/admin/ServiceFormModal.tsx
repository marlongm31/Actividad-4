import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { adminApi } from '../../api/adminApi';
import type { Service } from '../../types';
import { useToast } from '../../context/ToastContext';
import { normalizeError } from '../../utils/errors';
import { Modal } from '../ui/Modal';
import { Button } from '../ui/Button';
import { FormField } from '../ui/FormField';

interface ServiceFormModalProps {
  open: boolean;
  service: Service | null;
  onClose: () => void;
  onSaved: () => void;
}

interface ServiceForm {
  name: string;
  description: string;
  price: string;
  durationMinutes: string;
  active: boolean;
}

const EMPTY_FORM: ServiceForm = {
  name: '',
  description: '',
  price: '',
  durationMinutes: '45',
  active: true,
};

export function ServiceFormModal({ open, service, onClose, onSaved }: ServiceFormModalProps) {
  const { notify } = useToast();
  const [form, setForm] = useState<ServiceForm>(EMPTY_FORM);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!open) {
      return;
    }
    setErrors({});
    setForm(
      service
        ? {
            name: service.name,
            description: service.description ?? '',
            price: `${service.price}`,
            durationMinutes: `${service.durationMinutes}`,
            active: service.active,
          }
        : EMPTY_FORM,
    );
  }, [open, service]);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    const validation: Record<string, string> = {};
    if (!form.name.trim()) {
      validation.name = 'El nombre es obligatorio';
    }
    const price = Number(form.price);
    if (!form.price.trim() || Number.isNaN(price) || price < 0) {
      validation.price = 'Ingresa un precio valido';
    }
    const duration = Number(form.durationMinutes);
    if (Number.isNaN(duration) || duration < 15 || duration > 240) {
      validation.durationMinutes = 'La duracion debe estar entre 15 y 240 minutos';
    }
    setErrors(validation);
    if (Object.keys(validation).length > 0) {
      return;
    }

    setSaving(true);
    try {
      const payload = {
        name: form.name.trim(),
        description: form.description.trim(),
        price,
        durationMinutes: duration,
        active: form.active,
      };
      if (service) {
        await adminApi.updateService(service.id, payload);
        notify('Servicio actualizado correctamente', 'success');
      } else {
        await adminApi.createService(payload);
        notify('Servicio creado correctamente', 'success');
      }
      onSaved();
      onClose();
    } catch (caught) {
      const normalized = normalizeError(caught);
      setErrors(normalized.fieldErrors);
      notify(normalized.message, 'error');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      open={open}
      title={service ? 'Editar servicio' : 'Nuevo servicio'}
      description="Define el nombre, precio y duracion que veran los clientes."
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={saving}>
            Cancelar
          </Button>
          <Button type="submit" form="service-form" loading={saving}>
            {service ? 'Guardar cambios' : 'Crear servicio'}
          </Button>
        </>
      }
    >
      <form id="service-form" className="form-grid" onSubmit={(event) => void submit(event)} noValidate>
        <FormField label="Nombre" htmlFor="service-name" required error={errors.name}>
          <input
            id="service-name"
            value={form.name}
            maxLength={120}
            placeholder="Ej. Corte clasico"
            onChange={(event) => setForm({ ...form, name: event.target.value })}
          />
        </FormField>

        <FormField
          label="Descripcion"
          htmlFor="service-description"
          error={errors.description}
          hint="Se muestra en la pagina publica."
        >
          <textarea
            id="service-description"
            rows={3}
            maxLength={500}
            value={form.description}
            placeholder="Describe brevemente el servicio"
            onChange={(event) => setForm({ ...form, description: event.target.value })}
          />
        </FormField>

        <div className="form-grid form-grid--two">
          <FormField label="Precio (COP)" htmlFor="service-price" required error={errors.price}>
            <input
              id="service-price"
              type="number"
              min={0}
              step={500}
              value={form.price}
              placeholder="25000"
              onChange={(event) => setForm({ ...form, price: event.target.value })}
            />
          </FormField>
          <FormField
            label="Duracion (minutos)"
            htmlFor="service-duration"
            required
            error={errors.durationMinutes}
          >
            <input
              id="service-duration"
              type="number"
              min={15}
              max={240}
              step={5}
              value={form.durationMinutes}
              onChange={(event) => setForm({ ...form, durationMinutes: event.target.value })}
            />
          </FormField>
        </div>

        <label className="switch-field">
          <input
            type="checkbox"
            checked={form.active}
            onChange={(event) => setForm({ ...form, active: event.target.checked })}
          />
          <span className="switch" aria-hidden="true" />
          <span>
            Servicio activo
            <small>Los servicios inactivos no se muestran a los clientes.</small>
          </span>
        </label>
      </form>
    </Modal>
  );
}
