import { useNumberFormat } from "@react-input/number-format";

export function useCurrencyMask() {
  return useNumberFormat({
    locales: "pt-BR",
    format: "currency",
    currency: "BRL",
    maximumFractionDigits: 2,
  });
}