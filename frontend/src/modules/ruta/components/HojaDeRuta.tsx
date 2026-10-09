import { estadoDeHito, hitoActual, progresoDeNodo } from '../lib';
import type { Inscripcion, Ruta } from '../model';
import { HitoItem } from './HitoItem';

type HojaDeRutaProps = { ruta: Ruta; inscripcion: Inscripcion };

export function HojaDeRuta({ ruta, inscripcion }: HojaDeRutaProps) {
  const actual = hitoActual(ruta, inscripcion);

  return (
    <ol className="flex flex-col gap-2.5">
      {ruta.hitos.map((hito) => (
        <HitoItem
          key={hito.id}
          hito={hito}
          estado={estadoDeHito(hito, actual)}
          progresoDe={(nodoId) => progresoDeNodo(inscripcion, nodoId)}
        />
      ))}
    </ol>
  );
}
