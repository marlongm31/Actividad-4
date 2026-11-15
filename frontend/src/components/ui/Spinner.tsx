import { Loader2 } from 'lucide-react';

export function Spinner({ label = 'Cargando...' }: { label?: string }) {
  return (
    <div className="spinner" role="status">
      <Loader2 className="spin" size={26} aria-hidden="true" />
      <span>{label}</span>
    </div>
  );
}

export function InlineSpinner() {
  return <Loader2 className="spin" size={18} aria-hidden="true" />;
}
