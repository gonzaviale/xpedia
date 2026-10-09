import { cn } from '@/shared/lib/cn';

export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'solid';
export type ButtonSize = 'md' | 'sm';

const base =
  'inline-flex shrink-0 cursor-pointer items-center justify-center gap-1.5 rounded-md border border-transparent font-medium leading-tight whitespace-nowrap text-fg no-underline transition-colors disabled:cursor-not-allowed disabled:opacity-45';

const variants: Record<ButtonVariant, string> = {
  primary: 'border-accent text-accent hover:bg-accent/12 hover:text-accent active:bg-accent/22',
  secondary: 'border-fg/16 hover:bg-fg/7 hover:text-fg active:bg-fg/14',
  ghost: 'px-1 text-accent hover:bg-accent/10 hover:text-accent active:bg-accent/18',
  solid: 'border-accent-600 bg-accent-700 text-white hover:bg-accent-600 hover:text-white',
};

const sizes: Record<ButtonSize, string> = {
  md: 'px-2.5 py-1.5 text-sm',
  sm: 'px-2 py-1 text-[12.5px]',
};

type ButtonClassesOptions = { variant?: ButtonVariant; size?: ButtonSize; className?: string };

export function buttonClasses({
  variant = 'secondary',
  size = 'md',
  className,
}: ButtonClassesOptions = {}) {
  return cn(base, variants[variant], sizes[size], className);
}
