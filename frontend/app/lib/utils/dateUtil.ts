const DATE_TIME_FORMATTER = new Intl.DateTimeFormat("pt-BR", {
  day: "2-digit",
  month: "2-digit",
  year: "numeric",
  hour: "2-digit",
  minute: "2-digit",
  timeZone: "America/Sao_Paulo",
});

function toDate(value: string | Date): Date {
  return typeof value === "string" ? new Date(value) : value;
}

export const DateUtil = {
  formatDateTime(value: string | Date): string {
    const date = toDate(value);

    if (Number.isNaN(date.getTime())) {
      return "";
    }

    return DATE_TIME_FORMATTER.format(date);
  },
};
