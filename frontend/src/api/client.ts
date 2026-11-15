import axios from 'axios';

export const SESSION_KEY = 'barberturn.session';

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

http.interceptors.request.use((config) => {
  const raw = localStorage.getItem(SESSION_KEY);
  if (raw) {
    try {
      const session = JSON.parse(raw) as { token?: string };
      if (session.token) {
        config.headers.Authorization = `Bearer ${session.token}`;
      }
    } catch {
      localStorage.removeItem(SESSION_KEY);
    }
  }
  return config;
});
