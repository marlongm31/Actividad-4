import axios from 'axios';
import type { ApiErrorBody } from '../types';

export interface NormalizedError {
  message: string;
  fieldErrors: Record<string, string>;
}

export function normalizeError(error: unknown): NormalizedError {
  if (axios.isAxiosError<ApiErrorBody>(error)) {
    const body = error.response?.data;
    return {
      message:
        body?.message ??
        'No se pudo completar la operacion. Revisa tu conexion e intenta de nuevo.',
      fieldErrors: body?.fieldErrors ?? {},
    };
  }
  return {
    message: 'Ocurrio un error inesperado. Intenta nuevamente.',
    fieldErrors: {},
  };
}
