import { http } from './client';
import type {
  Appointment,
  Availability,
  BookAppointmentPayload,
  Service,
} from '../types';

export const publicApi = {
  async listServices(): Promise<Service[]> {
    const { data } = await http.get<Service[]>('/public/services');
    return data;
  },

  async getAvailability(date: string, serviceId: number): Promise<Availability> {
    const { data } = await http.get<Availability>('/public/availability', {
      params: { date, serviceId },
    });
    return data;
  },

  async bookAppointment(payload: BookAppointmentPayload): Promise<Appointment> {
    const { data } = await http.post<Appointment>('/public/appointments', payload);
    return data;
  },
};
