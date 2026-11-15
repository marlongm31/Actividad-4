import { describe, expect, it } from 'vitest';
import { formatDuration, formatMoney } from './money';

describe('money utils', () => {
  it('formats Colombian pesos without decimals', () => {
    const formatted = formatMoney(25000);
    expect(formatted).toContain('25.000');
    expect(formatted).not.toContain(',00');
  });

  it('formats durations under one hour in minutes', () => {
    expect(formatDuration(15)).toBe('15 min');
    expect(formatDuration(45)).toBe('45 min');
  });

  it('formats durations in hours', () => {
    expect(formatDuration(60)).toBe('1 hora');
    expect(formatDuration(120)).toBe('2 horas');
    expect(formatDuration(90)).toBe('1 h 30 min');
  });
});
