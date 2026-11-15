import { describe, expect, it } from 'vitest';
import { STATUS_META, statusMeta } from './status';

describe('status utils', () => {
  it('maps every appointment status to a Spanish label', () => {
    expect(statusMeta('PENDING').label).toBe('Pendiente');
    expect(statusMeta('CONFIRMED').label).toBe('Confirmada');
    expect(statusMeta('COMPLETED').label).toBe('Completada');
    expect(statusMeta('CANCELLED').label).toBe('Cancelada');
    expect(Object.keys(STATUS_META)).toHaveLength(4);
  });

  it('returns a safe fallback for unknown statuses', () => {
    expect(statusMeta('UNKNOWN').tone).toBe('neutral');
    expect(statusMeta('UNKNOWN').label).toBe('UNKNOWN');
  });
});
