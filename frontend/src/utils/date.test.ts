import { describe, expect, it } from 'vitest';
import {
  addDays,
  formatLongDate,
  formatMonthShort,
  formatTime,
  formatTimeRange,
  nextDays,
  parseISODate,
  relativeDayLabel,
  toISODate,
} from './date';

describe('date utils', () => {
  it('converts a Date to an ISO date without timezone shifts', () => {
    expect(toISODate(new Date(2026, 10, 16))).toBe('2026-11-16');
  });

  it('parses an ISO date using local time', () => {
    const date = parseISODate('2026-11-16');
    expect(date.getFullYear()).toBe(2026);
    expect(date.getMonth()).toBe(10);
    expect(date.getDate()).toBe(16);
  });

  it('adds days without mutating the original date', () => {
    const base = new Date(2026, 10, 16);
    expect(toISODate(addDays(base, 5))).toBe('2026-11-21');
    expect(toISODate(base)).toBe('2026-11-16');
  });

  it('builds a list of consecutive ISO dates', () => {
    const days = nextDays(3, new Date(2026, 10, 16));
    expect(days).toEqual(['2026-11-16', '2026-11-17', '2026-11-18']);
  });

  it('formats long dates in Spanish', () => {
    const formatted = formatLongDate('2026-11-16');
    expect(formatted.toLowerCase()).toContain('lunes');
    expect(formatted).toContain('16');
    expect(formatted).toContain('noviembre');
  });

  it('formats times in 12 hour format', () => {
    expect(formatTime('09:00')).toBe('9:00 a. m.');
    expect(formatTime('13:30')).toBe('1:30 p. m.');
    expect(formatTime('00:15')).toBe('12:15 a. m.');
    expect(formatTimeRange('10:00:00', '11:00:00')).toBe('10:00 a. m. - 11:00 a. m.');
  });

  it('labels relative days and month abbreviations', () => {
    expect(relativeDayLabel(toISODate(new Date()))).toBe('Hoy');
    expect(formatMonthShort('2026-11-16').toLowerCase()).toContain('nov');
  });
});
