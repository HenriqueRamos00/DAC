import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatCpf(value: string): string {
  const digits = value.replace(/\D/g, "").slice(0, 11);

  if (digits.length !== 11) {
    return value;
  }

  return digits.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
}

export function formatCep(value: string): string {
  const digits = value.replace(/\D/g, "").slice(0, 8);

  if (digits.length !== 8) {
    return value;
  }

  return digits.replace(/(\d{2})(\d{3})(\d{3})/, "$1.$2-$3");
}
