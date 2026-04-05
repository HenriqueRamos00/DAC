
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { getSessionAutenticada } from "~/services/auth.server";
import { Clipboard, LayoutDashboard } from "lucide-react";
import type { Cliente } from "~/models/dto/Cliente";
import { TabelaAdminClientes } from "~/features/tabela-admin-clientes/tabela-admin-clientes";
import type { Route } from "./+types/relatorio-clientes";

export function meta({}: Route.MetaArgs) {
    return [
        { title: "Relatório de clientes" },
        { name: "description", content: "Relatório de clientes" },
    ];
}

export async function loader({ request }: Route.LoaderArgs) {
    const { apiClient } = await getSessionAutenticada(request);
    const response = await apiClient.get("/clientes");

    if (!response.ok) {
        throw new Response("Erro ao carregar tela de relatório de clientes", { status: response.status });
    }

    const clientes = (await response.json()) as Cliente[];
    const clientesOrdenados = clientes.slice().sort((a, b) =>
        a.nome.localeCompare(b.nome, "pt-BR")
    );

    return { clientes: clientesOrdenados };
}

export default function RelatorioClientes({ loaderData }: Route.ComponentProps) {
    const { clientes } = loaderData;

    return (
        <div className="flex flex-col gap-6">
            <AppBreadcrumb
                items={[
                    { label: "Home", href: "/" },
                    { label: "Relatório de clientes" },
                ]}
            />

            <h1 className="text-sm text-primary uppercase flex items-center gap-2">
                Relatório de clientes
            </h1>

            <div className="flex flex-col bg-card border-3 border-border p-4 gap-4">
                <div className="border-b border-border pb-4">
                    <span className="text-primary text-xs uppercase flex items-center gap-2">
                        <Clipboard size={16}/> Relatório completo
                    </span>
                </div>

                <TabelaAdminClientes clientes={clientes} />
            </div>
        </div>
    );
}
