import { Check, CheckCheck, Mail, Phone, X } from 'lucide-react';
import type { Appointment, AppointmentStatus } from '../../types';
import { formatTime, formatTimeRange } from '../../utils/date';
import { formatMoney } from '../../utils/money';
import { StatusBadge } from '../ui/Badge';

interface AppointmentTableProps {
  appointments: Appointment[];
  onChangeStatus: (appointment: Appointment, status: AppointmentStatus) => void;
  busyId?: number | null;
}

export function AppointmentTable({ appointments, onChangeStatus, busyId }: AppointmentTableProps) {
  return (
    <div className="table-wrap">
      <table className="data-table">
        <thead>
          <tr>
            <th>Horario</th>
            <th>Cliente</th>
            <th>Servicio</th>
            <th>Contacto</th>
            <th>Estado</th>
            <th className="data-table__actions">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {appointments.map((appointment) => {
            const busy = busyId === appointment.id;
            return (
              <tr key={appointment.id}>
                <td>
                  <div className="time-cell">
                    <strong>{formatTime(appointment.startTime)}</strong>
                    <span>{formatTimeRange(appointment.startTime, appointment.endTime)}</span>
                  </div>
                </td>
                <td>
                  <div className="customer-cell">
                    <strong>{appointment.customerName}</strong>
                    {appointment.notes ? <span>{appointment.notes}</span> : null}
                  </div>
                </td>
                <td>
                  <div className="service-cell">
                    <strong>{appointment.serviceName}</strong>
                    <span>{formatMoney(appointment.servicePrice)}</span>
                  </div>
                </td>
                <td>
                  <div className="contact-cell">
                    <a href={`tel:${appointment.customerPhone}`}>
                      <Phone size={14} />
                      {appointment.customerPhone}
                    </a>
                    {appointment.customerEmail ? (
                      <a href={`mailto:${appointment.customerEmail}`}>
                        <Mail size={14} />
                        {appointment.customerEmail}
                      </a>
                    ) : null}
                  </div>
                </td>
                <td>
                  <StatusBadge status={appointment.status} />
                </td>
                <td>
                  <div className="row-actions">
                    {appointment.status === 'PENDING' ? (
                      <button
                        type="button"
                        className="icon-btn icon-btn--success"
                        title="Confirmar cita"
                        aria-label="Confirmar cita"
                        disabled={busy}
                        onClick={() => onChangeStatus(appointment, 'CONFIRMED')}
                      >
                        <Check size={16} />
                      </button>
                    ) : null}
                    {appointment.status === 'CONFIRMED' ? (
                      <button
                        type="button"
                        className="icon-btn icon-btn--info"
                        title="Marcar como completada"
                        aria-label="Marcar como completada"
                        disabled={busy}
                        onClick={() => onChangeStatus(appointment, 'COMPLETED')}
                      >
                        <CheckCheck size={16} />
                      </button>
                    ) : null}
                    {appointment.status === 'PENDING' || appointment.status === 'CONFIRMED' ? (
                      <button
                        type="button"
                        className="icon-btn icon-btn--danger"
                        title="Cancelar cita"
                        aria-label="Cancelar cita"
                        disabled={busy}
                        onClick={() => onChangeStatus(appointment, 'CANCELLED')}
                      >
                        <X size={16} />
                      </button>
                    ) : (
                      <span className="row-actions__empty">Sin acciones</span>
                    )}
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
