const LANG = "pt-BR";
const CURRENCY = "BRL";

function getFormattedCurrency(value: number):string {
  return value.toLocaleString(
    LANG,
    {
      style: "currency",
      currency: CURRENCY,
    }
  );
}

function parseCurrency(masked: string): number {
  const digits = masked.replace(/[R$,.]/g,"").trim();
  if (!digits) return NaN;
  return Number(digits) / 100;
}

export { getFormattedCurrency, parseCurrency }