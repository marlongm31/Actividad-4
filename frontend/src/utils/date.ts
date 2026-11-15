const LONG_DATE = new Intl.DateTimeFormat('es-CO', {
  weekday: 'long',
  day: 'numeric',
  month: 'long',
});

const SHORT_DATE = new Intl.DateTimeFormat('es-CO', {
  day: '2-digit',
  month: 'short',
});

const WEEKDAY = new Intl.DateTimeFormat('es-CO', { weekday: 'short' });

const MONTH_SHORT = new Intl.DateTimeFormat('es-CO', { month: 'short' });

export function toISODate(date: Date): string {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, '0');
  const day = `${date.getDate()}`.padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function parseISODate(value: string): Date {
  const [year, month, day] = value.split('-').map(Number);
  return new Date(year, month - 1, day);
}

export function todayISO(): string {
  return toISODate(new Date());
}

export function addDays(date: Date, days: number): Date {
  const copy = new Date(date);
  copy.setDate(copy.getDate() + days);
  return copy;
}

export function nextDays(amount: number, start = new Date()): string[] {
  return Array.from({ length: amount }, (_, index) => toISODate(addDays(start, index)));
}

export function formatLongDate(value: string): string {
  return capitalize(LONG_DATE.format(parseISODate(value)));
}

export function formatShortDate(value: string): string {
  return SHORT_DATE.format(parseISODate(value));
}

export function formatWeekday(value: string): string {
  return capitalize(WEEKDAY.format(parseISODate(value)).replace('.', ''));
}

export function formatMonthShort(value: string): string {
  return MONTH_SHORT.format(parseISODate(value)).replace('.', '');
}

export function dayNumber(value: string): string {
  return `${parseISODate(value).getDate()}`;
}

export function relativeDayLabel(value: string): string {
  const today = todayISO();
  if (value === today) {
    return 'Hoy';
  }
  if (value === toISODate(addDays(new Date(), 1))) {
    return 'Manana';
  }
  return formatWeekday(value);
}

export function formatTime(value: string): string {
  const [hours, minutes] = value.split(':').map(Number);
  const suffix = hours >= 12 ? 'p. m.' : 'a. m.';
  const hour12 = hours % 12 === 0 ? 12 : hours % 12;
  return `${hour12}:${`${minutes}`.padStart(2, '0')} ${suffix}`;
}

export function formatTimeRange(start: string, end: string): string {
  return `${formatTime(start)} - ${formatTime(end)}`;
}

export function capitalize(value: string): string {
  return value.charAt(0).toUpperCase() + value.slice(1);
}
