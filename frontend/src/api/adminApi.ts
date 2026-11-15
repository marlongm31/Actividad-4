import { http } from './client';
import type {
  Appointment,
  AppointmentStatus,
  AuthResponse,
  BusinessHours,
  DashboardStats,
  Service,
} from '../types';

export interface ServicePayload {
  name: string;
  description: string;
  price: number;
  durationMinutes: number;
  active: boolean;
}

export interface ScheduleDayPayload {
  dayOfWeek: number;
  openTime: string;
  closeTime: string;
  active: boolean;
}

export const authApi = {
  async login(email: string, password: string): Promise<AuthResponse> {
    const { data } = await http.post<AuthResponse>('/auth/login', { email, password });
    return data;
  },
};

export const adminApi = {
  async listAppointments(date?: string, status?: AppointmentStatus): Promise<Appointment[]> {
    const { data } = await http.get<Appointment[]>('/admin/appointments', {
      params: { date: date || undefined, status: status || undefined },
    });
    return data;
  },

  async changeStatus(id: number, status: AppointmentStatus): Promise<Appointment> {
    const { data } = await http.patch<Appointment>(`/admin/appointments/${id}/status`, { status });
    return data;
  },

  async dashboard(date?: string): Promise<DashboardStats> {
    const { data } = await http.get<DashboardStats>('/admin/dashboard', {
      params: { date: date || undefined },
    });
    return data;
  },

  async listServices(): Promise<Service[]> {
    const { data } = await http.get<Service[]>('/admin/services');
    return data;
  },

  async createService(payload: ServicePayload): Promise<Service> {
    const { data } = await http.post<Service>('/admin/services', payload);
    return data;
  },

  async updateService(id: number, payload: ServicePayload): Promise<Service> {
    const { data } = await http.put<Service>(`/admin/services/${id}`, payload);
    return data;
  },

  async deactivateService(id: number): Promise<void> {
    await http.delete(`/admin/services/${id}`);
  },

  async getSchedule(): Promise<BusinessHours[]> {
    const { data } = await http.get<BusinessHours[]>('/admin/business-hours');
    return data;
  },

  async updateSchedule(days: ScheduleDayPayload[]): Promise<BusinessHours[]> {
    const { data } = await http.put<BusinessHours[]>('/admin/business-hours', { days });
    return data;
  },
};
