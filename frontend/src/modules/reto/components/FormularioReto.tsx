import { useForm } from '@tanstack/react-form';
import { PaperPlaneTiltIcon } from '@phosphor-icons/react';
import { ApiError } from '@/shared/api';
import { Button, Feedback, Spinner, TextareaField } from '@/shared/ui';
import { useEnviarIntento, type DestinoIntento } from '../api';
import { enviarIntentoSchema, type Intento } from '../model';
import { useBorradoresStore } from '../store';

type FormularioRetoProps = { destino: DestinoIntento; onEnviado: (intento: Intento) => void };

export function FormularioReto({ destino, onEnviado }: FormularioRetoProps) {
  const actividadId = destino.retoId;
  const borrador = useBorradoresStore((s) => s.porActividad[actividadId] ?? '');
  const guardar = useBorradoresStore((s) => s.guardar);
  const descartar = useBorradoresStore((s) => s.descartar);
  const enviar = useEnviarIntento(destino);

  const form = useForm({
    defaultValues: { respuesta: borrador },
    validators: { onSubmit: enviarIntentoSchema },
    onSubmit: async ({ value }) => {
      const intento = await enviar.mutateAsync(value);
      descartar(actividadId);
      onEnviado(intento);
    },
  });

  return (
    <form
      className="flex flex-col gap-3"
      noValidate
      onSubmit={(event) => {
        event.preventDefault();
        void form.handleSubmit();
      }}
    >
      <form.Field
        name="respuesta"
        validators={{ onBlur: enviarIntentoSchema.shape.respuesta }}
        listeners={{ onChange: ({ value }) => guardar(actividadId, value) }}
      >
        {(field) => (
          <TextareaField
            label="Tu respuesta"
            hint="Escribila como si le contestaras por chat. Tu borrador se guarda solo."
            value={field.state.value}
            error={field.state.meta.errors[0]?.message}
            disabled={enviar.isPending}
            onBlur={field.handleBlur}
            onChange={(event) => field.handleChange(event.target.value)}
          />
        )}
      </form.Field>
      {enviar.error && (
        <Feedback tone="bad" role="alert">
          {enviar.error instanceof ApiError
            ? enviar.error.message
            : 'No pudimos enviar tu respuesta.'}
        </Feedback>
      )}
      <div className="flex items-center justify-between gap-3">
        {enviar.isPending ? <Spinner label="El profesor de IA está evaluando…" /> : <span />}
        <Button type="submit" variant="solid" disabled={enviar.isPending}>
          <PaperPlaneTiltIcon aria-hidden />
          Enviar al profesor de IA
        </Button>
      </div>
    </form>
  );
}
