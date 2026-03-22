import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { useState } from "react";
import { Button } from "~/components/ui/button";
import { Calendar } from "~/components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "~/components/ui/popover";
import { format } from "date-fns";
import { ptBR } from "date-fns/locale";
import { CalendarIcon } from "lucide-react";
import type { DateRange } from "react-day-picker";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Extrato" },
    { name: "description", content: "Extrato" },
  ];
}

export default function Extrato() {
  const [date, setDate] = useState<DateRange | undefined>();

  function handleFiltrar() {
    // TODO: buscar extrato pela API com filtros
  }

  function handleLimpar() {
    setDate(undefined);
  }

  return (
    <div className="flex flex-col gap-6 uppercase font-mono font-bold text-xs">
      <AppBreadcrumb
        items={[
          { label: "Retro-Bank", href: "/" },
          { label: "Cliente", href: "/cliente" },
          { label: "Extrato" },
        ]}
      />

      <h1 className="text-xl text-primary font-pixel uppercase">EXTRATO</h1>

      {/* Filtros */}
      <div className="flex flex-col bg-card border-3 border-border p-4 gap-4 max-w-3xl">
        <div className="border-b border-border pb-4">
          <span className="text-primary font-pixel uppercase">FILTROS</span>
        </div>

        <label className="text-sm text-muted-foreground uppercase">
          PERÍODO
        </label>

        <div className="flex flex-row gap-4 items-center">
          <Popover>
            <PopoverTrigger
              render={
                <Button
                  variant="outline"
                  className="w-72 h-10 justify-start px-2.5 font-normal normal-case"
                />
              }
            >
              <CalendarIcon className="mr-2 size-4" />
              {date?.from ? (
                date.to ? (
                  <>
                    {format(date.from, "dd MMM yyyy", { locale: ptBR })} -{" "}
                    {format(date.to, "dd MMM yyyy", { locale: ptBR })}
                  </>
                ) : (
                  format(date.from, "dd MMM yyyy", { locale: ptBR })
                )
              ) : (
                <span className="text-muted-foreground">
                  Selecione um período
                </span>
              )}
            </PopoverTrigger>
            <PopoverContent className="w-auto p-0" align="start">
              <Calendar
                mode="range"
                defaultMonth={date?.from}
                selected={date}
                onSelect={setDate}
                numberOfMonths={2}
                locale={ptBR}
                className="[--cell-size:--spacing(10)]"
                classNames={{
                  weekday: "flex-1 rounded-(--cell-radius) text-[0.65rem] font-mono font-normal text-muted-foreground select-none",
                  range_start: "bg-primary/30 rounded-l-md",
                  range_middle: "bg-primary/15 rounded-none",
                  range_end: "bg-primary/30 rounded-r-md",
                }}
              />
            </PopoverContent>
          </Popover>

          <Button type="button" variant="confirm" className="h-10" onClick={handleFiltrar}>
            ► FILTRAR
          </Button>
          <Button type="button" variant="outline" className="h-10" onClick={handleLimpar}>
            LIMPAR
          </Button>
        </div>
      </div>
    </div>
  );
}
