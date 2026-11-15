import { Link } from 'react-router-dom';
import { ArrowLeft, Scissors } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { Logo } from '../components/ui/Logo';

export function NotFoundPage() {
  return (
    <div className="not-found">
      <div className="not-found__card">
        <Logo />
        <span className="not-found__icon">
          <Scissors size={28} />
        </span>
        <h1>Esta pagina se fue de corte</h1>
        <p>No encontramos lo que buscas. Vuelve al inicio y retoma el estilo.</p>
        <Link to="/">
          <Button icon={<ArrowLeft size={17} />}>Volver al inicio</Button>
        </Link>
      </div>
    </div>
  );
}
