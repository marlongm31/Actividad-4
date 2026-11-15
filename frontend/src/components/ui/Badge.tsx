import type { ReactNode } from 'react';
import { statusMeta } from '../../utils/status';

export function StatusBadge({ status }: { status: string }) {
  const meta = statusMeta(status);
  return (
    <span className={`badge badge--${meta.tone}`}>
      <span className="badge__dot" aria-hidden="true" />
      {meta.label}
    </span>
  );
}

export function Badge({ children, tone = 'neutral' }: { children: ReactNode; tone?: string }) {
  return <span className={`badge badge--${tone}`}>{children}</span>;
}
