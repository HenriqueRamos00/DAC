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

export { getFormattedCurrency }