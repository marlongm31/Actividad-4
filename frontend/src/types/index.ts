export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface Service {
  id: number;
  name: string;
  description: string | null;
  price: number;
  durationMinutes: number;
  active: boolean;
}

export interface TimeSlot {
  startTime: string;
  endTime: string;
}

export interface Availability {
  date: string;
  serviceId: number;
  slots: TimeSlot[];
}

export interface Appointment {
  id: number;
  serviceId: number;
  serviceName: string;
  servicePrice: number;
  durationMinutes: number;
  customerName: string;
  customerPhone: string;
  customerEmail: string | null;
  notes: string | null;
  date: string;
  startTime: string;
  endTime: string;
  status: AppointmentStatus;
  statusLabel: string;
  createdAt: string;
}

export interface DashboardStats {
  date: string;
  todayAppointments: number;
  pending: number;
  confirmed: number;
  completed: number;
  cancelled: number;
  estimatedRevenue: number;
}

export interface BusinessHours {
  dayOfWeek: number;
  dayName: string;
  openTime: string;
  closeTime: string;
  active: boolean;
}

export interface AuthResponse {
  token: string;
  name: string;
  email: string;
  expiresInSeconds: number;
}

export interface AdminSession {
  token: string;
  name: string;
  email: string;
}

export interface BookAppointmentPayload {
  serviceId: number;
  date: string;
  startTime: string;
  customerName: string;
  customerPhone: string;
  customerEmail?: string;
  notes?: string;
}

export interface ApiErrorBody {
  status: number;
  error: string;
  message: string;
  fieldErrors?: Record<string, string> | null;
}
