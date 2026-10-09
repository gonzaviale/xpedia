import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { diagnostico, reto, ruta } from '@/mocks/db/atencion';
import { estado } from '@/mocks/db/estado';
import { crearInscripcion } from '@/mocks/diagnosticar';
import { renderApp } from '@/test/render-app';

const ESPERA = { timeout: 5000 };

function sembrarInscripcion() {
  estado.inscripcion = crearInscripcion(
    {
      rutaSlug: 'atencion-al-cliente-remota',
      objetivo: 'CAMBIAR',
      ritmoMin: 20,
      respuestas: diagnostico.preguntas.map((pregunta) => ({
        preguntaId: pregunta.id,
        elegida: 1,
        confianza: 'SABIA' as const,
      })),
    },
    ruta,
  );
}

describe('flujo de la primera ruta', () => {
  it('lleva a la bienvenida cuando no hay inscripción', async () => {
    renderApp('/');

    expect(await screen.findByText('¿Qué querés lograr?', {}, ESPERA)).toBeInTheDocument();
  });

  it('completa el diagnóstico y muestra la ruta', async () => {
    const user = userEvent.setup();
    renderApp('/bienvenida');

    await user.click(await screen.findByRole('button', { name: /Cambiar de rubro/ }));
    await user.click(screen.getByRole('button', { name: 'Siguiente' }));
    await user.click(screen.getByRole('button', { name: /Empezar el diagnóstico/ }));

    for (let pregunta = 0; pregunta < diagnostico.preguntas.length; pregunta++) {
      const [opcionCorrecta] = await screen.findAllByRole('button', { name: /^b / }, ESPERA);
      if (!opcionCorrecta) throw new Error('No se renderizó la opción b');
      await user.click(opcionCorrecta);
      await user.click(screen.getByRole('button', { name: 'Lo sabía' }));
      const avanzar = pregunta === diagnostico.preguntas.length - 1 ? 'Ver resultado' : 'Siguiente';
      await user.click(screen.getByRole('button', { name: avanzar }));
    }

    expect(
      await screen.findByText('Tu mapa de habilidades transferibles', {}, ESPERA),
    ).toBeInTheDocument();

    await user.click(screen.getByRole('link', { name: /Ver mi ruta/ }));

    expect(
      await screen.findByRole('heading', { name: 'Atención al cliente remota' }, ESPERA),
    ).toBeInTheDocument();
    expect(screen.getByText('Hito 1')).toBeInTheDocument();
  });

  it('muestra una microlección con su fuente y licencia', async () => {
    sembrarInscripcion();
    renderApp('/nodos/nodo-a3/leccion');

    expect(
      await screen.findByRole(
        'heading',
        { name: /Escuchar, reconocer, resolver, confirmar/ },
        ESPERA,
      ),
    ).toBeInTheDocument();
    expect(screen.getByText(/CC BY 4.0/)).toBeInTheDocument();
  });

  it('valida el largo de la respuesta antes de enviar el reto', async () => {
    const user = userEvent.setup();
    sembrarInscripcion();
    renderApp('/nodos/nodo-a3/reto');

    await user.type(await screen.findByLabelText('Tu respuesta', {}, ESPERA), 'Hola');
    await user.click(screen.getByRole('button', { name: /Enviar al profesor de IA/ }));

    expect(await screen.findByText(/Escribí al menos 30 caracteres/)).toBeInTheDocument();
  });

  it('envía el reto y muestra la devolución del profesor de IA', async () => {
    const user = userEvent.setup();
    sembrarInscripcion();
    renderApp(`/nodos/${reto.nodoId}/reto`);

    await user.click(await screen.findByLabelText('Tu respuesta', {}, ESPERA));
    await user.paste(
      'Tenés razón, escribiste dos veces y no te respondimos, y el domingo corrés. Me ocupo yo, dame 2 minutos. ' +
        'El correo no nos da fecha, no te lo puedo asegurar. Mañana desde las 14 podés retirar el gris en Rosario Centro, ' +
        'o esperar el negro y te reintegramos todo. Quedamos así: te escribo mañana a las 15 para confirmar.',
    );
    await user.click(screen.getByRole('button', { name: /Enviar al profesor de IA/ }));

    expect(
      await screen.findByText('Devolución del profesor de IA', {}, ESPERA),
    ).toBeInTheDocument();
    expect(screen.getByText('Aprobado')).toBeInTheDocument();
    expect(screen.getByRole('table', { name: /Puntaje por criterio/ })).toBeInTheDocument();
  });
});
