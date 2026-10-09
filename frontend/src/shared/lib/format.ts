const formatoFecha = new Intl.DateTimeFormat('es-AR', { day: 'numeric', month: 'long' });

export function formatearFecha(iso: string) {
  return formatoFecha.format(new Date(`${iso}T00:00:00`));
}

export function formatearMinutos(minutos: number) {
  if (minutos < 60) return `${minutos} min`;
  const horas = Math.floor(minutos / 60);
  const resto = minutos % 60;
  return resto === 0 ? `${horas} h` : `${horas} h ${resto} min`;
}
