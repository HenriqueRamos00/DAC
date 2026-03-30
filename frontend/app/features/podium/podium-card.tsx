import type { Cliente } from "~/models/dto/Cliente";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";

const medalhas = ["🥇", "🥈", "🥉"];
const medalCores = ["text-yellow-400", "text-gray-300", "text-amber-600"];

const sizeClasses = [
  "order-first md:order-2 md:-mt-8 py-10 px-8 text-base",
  "order-2 md:order-1 py-8 px-6 text-sm",
  "order-3 py-6 px-5 text-xs",
];

export function PodiumCard({
  cliente,
  posicao,
}: {
  cliente: Cliente;
  posicao: number;
}) {
  return (
    <div
      className={`flex flex-col items-center gap-2 bg-card border-3 border-border ${sizeClasses[posicao]}`}
    >
      <span className={posicao === 0 ? "text-4xl" : posicao === 1 ? "text-3xl" : "text-2xl"}>
        {medalhas[posicao]}
      </span>
      <span className={`text-xs font-mono font-bold ${medalCores[posicao]}`}>
        #{posicao + 1}
      </span>
      <span className="text-sm font-bold">{cliente.nome}</span>
      <span className="text-xs text-muted-foreground font-mono">{cliente.cpf}</span>
      <span className="text-xs text-muted-foreground">
        {cliente.cidade} - {cliente.estado}
      </span>
      <span className="text-sm font-mono font-bold text-primary mt-2">
        {getFormattedCurrency(parseFloat(cliente.saldo))}
      </span>
    </div>
  );
}

export { medalhas, medalCores };
