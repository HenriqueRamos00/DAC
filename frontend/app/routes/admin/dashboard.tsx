import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { getSessionAutenticada } from "~/services/auth.server";
import { LayoutDashboard, TrendingDown, TrendingUp, Users } from "lucide-react";
import { TabelaGerentes } from "~/features/tabela-gerentes/tabela-gerentes";
import type { GerenteDashboard, GerenteResumo } from "~/models/dto/GerenteResumo";
import Painel from "~/components/app-painel";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";

export function meta({}: Route.MetaArgs) {
    return [
        { title: "Dashboard Admin" },
        { name: "description", content: "Painel do administrador" },
    ];
}

export async function loader({ request }: Route.LoaderArgs) {
    const { apiClient } = await getSessionAutenticada(request);
    const response = await apiClient.get("/gerentes?filtro=dashboard");

    if (!response.ok) {
        throw new Response("Erro ao carregar dashboard", { status: response.status });
    }

    const dashboard = (await response.json()) as GerenteDashboard[];
    const gerentes = dashboard.map(toGerenteResumo);
    return { gerentes };
}

function toGerenteResumo(item: GerenteDashboard): GerenteResumo {
    return {
        cpf: item.gerente.cpf,
        nome: item.gerente.nome,
        email: item.gerente.email,
        quantidadeClientes: item.clientes.length,
        totalSaldoPositivo: item.saldo_positivo,
        totalSaldoNegativo: item.saldo_negativo,
    };
}

export default function AdminDashboard({ loaderData }: Route.ComponentProps) {
    const { gerentes } = loaderData;

    const totalClientes = gerentes.reduce((sum, g) => sum + (g.quantidadeClientes ?? 0), 0);
    const totalPositivo = gerentes.reduce((sum, g) => sum + (g.totalSaldoPositivo ?? 0), 0);
    const totalNegativo = gerentes.reduce((sum, g) => sum + (g.totalSaldoNegativo ?? 0), 0);

    return (
        <div className="flex min-w-0 flex-col gap-4">
            <AppBreadcrumb
                items={[
                    { label: "Home", href: "/" },
                    { label: "Administrador" },
                ]}
            />

            <h1 className="text-sm text-primary uppercase flex items-center gap-2">
                <LayoutDashboard size={16} /> PAINEL DO ADMINISTRADOR
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Painel
                    icon={Users}
                    title="Total de Clientes"
                    content={String(totalClientes)}
                    className="text-primary"
                />
                <Painel
                    icon={TrendingUp}
                    title="Saldo Positivo Total"
                    content={getFormattedCurrency(totalPositivo)}
                    className="text-(--manager)"
                />
                <Painel
                    icon={TrendingDown}
                    title="Saldo Negativo Total"
                    content={getFormattedCurrency(totalNegativo)}
                    className="text-destructive"
                />
            </div>

            <div className="flex min-w-0 flex-col bg-card border-3 border-border p-3 gap-3">
                <div className="border-b border-border pb-3">
                    <span className="text-primary text-xs uppercase flex items-center gap-2">
                        RESUMO POR GERENTE
                    </span>
                </div>

                <TabelaGerentes gerentes={gerentes} pageSize={6} compact />
            </div>
        </div>
    );
}
