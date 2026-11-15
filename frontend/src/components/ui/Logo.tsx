import { Scissors } from 'lucide-react';

export function Logo({ compact = false }: { compact?: boolean }) {
  return (
    <span className={`logo ${compact ? 'logo--compact' : ''}`}>
      <span className="logo__mark">
        <Scissors size={18} aria-hidden="true" />
      </span>
      <span className="logo__text">
        Barber<span>Turn</span>
      </span>
    </span>
  );
}
