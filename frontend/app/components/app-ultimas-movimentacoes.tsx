import { ChevronRightCircle, FileText } from "lucide-react";
import type { Extrato } from "~/models/dto/Extrato";
import { DateUtil } from "~/lib/utils/dateUtil";
import { buttonVariants } from "./ui/button";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import { Link } from "react-router";
import { cn } from "~/lib/utils";

interface UltimasMovimentacoesProps {
  extrato: Extrato;
  mostrarBotaoExtratoCompleto?: boolean;
}

export default function UltimasMovimentacoes({
  extrato,
  mostrarBotaoExtratoCompleto = true,
}: UltimasMovimentacoesProps) {
  const saldo = extrato.saldo;
  const conta = extrato.conta;
  const movimentacoes = extrato.movimentacoes;

  return (
    <div className="flex flex-col bg-sidebar border-6 p-2 text-ring">
      <div className="flex w-full items-center gap-4 border-b border-sidebar-border py-4 text-primary">
        <FileText size={20} className="" />
        <h3 className="uppercase font-mono font-bold">Últimas Movimentações</h3>
      </div>

      <div className="flex flex-col gap-2 py-2">
        {movimentacoes.map((movimentacao) => {
          const dataFormatada = DateUtil.formatDateTime(movimentacao.data);
          const ehEntrada = movimentacao.destino === conta;

          return (
            <div key={movimentacao.id} className="flex justify-between items-center p-2 bg-sidebar-ring/5">
              <div className="flex flex-col text-xs gap-1">
                <span className="font-mono text-sm text-green-200 uppercase">{movimentacao.tipo}</span>
                <span className="font-mono">{dataFormatada}</span>
              </div>

              <span className={`text-sm ${ehEntrada ? "text-chart-1" : "text-destructive"}`}>
                {ehEntrada ? "+" : "-"} {getFormattedCurrency(Number(movimentacao.valor))}
              </span>
            </div>
          );
        })}
      </div>

      {mostrarBotaoExtratoCompleto && (
        <div className="flex flex-col gap-4 sm:flex-row">
          <Link
            to="/cliente/extrato"
            className={cn(
              buttonVariants({ variant: "ghost" }),
              "font-mono uppercase border-border text-ring"
            )}
          >
            Ver Extrato Completo <ChevronRightCircle />
          </Link>
        </div>
      )}
    </div>
  );
}
