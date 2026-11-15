export const STATUS_META: Record<
  string,
  { label: string; tone: string; description: string }
> = {
  PENDING: {
    label: 'Pendiente',
    tone: 'warning',
    description: 'Esperando confirmacion del peluquero',
  },
  CONFIRMED: {
    label: 'Confirmada',
    tone: 'success',
    description: 'Cita confirmada y agendada',
  },
  COMPLETED: {
    label: 'Completada',
    tone: 'info',
    description: 'Servicio realizado',
  },
  CANCELLED: {
    label: 'Cancelada',
    tone: 'danger',
    description: 'Cita cancelada',
  },
};

export function statusMeta(status: string) {
  return STATUS_META[status] ?? { label: status, tone: 'neutral', description: '' };
}
