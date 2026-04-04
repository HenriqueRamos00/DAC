import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { getSessionAutenticada } from "~/services/auth.server";
import { LayoutDashboard} from "lucide-react";
import { TabelaAdminListarGerentes } from "~/features/tabela-admin-gerentes/tabela-listar-gerentes";
import type { Gerente } from "~/models/dto/Gerente";
import type { Route } from "./+types/listar-gerentes";
import { Button } from "~/components/ui/button";
import { data, NavLink, redirect } from "react-router";

export function meta({}: Route.MetaArgs) {
    return [
        { title: "Gerentes" },
        { name: "description", content: "Gerentes" },
    ];
}

export async function loader({ request }: Route.LoaderArgs) {
    const { apiClient } = await getSessionAutenticada(request);
    const response = await apiClient.get("/gerentes");

    if (!response.ok) {
        throw new Response("Erro ao carregar tela de gerentes", { status: response.status });
    }
    const gerentes = (await response.json()) as Gerente[];
    const gerentesOrdenados = gerentes.slice().sort((a,b) =>
        a.nome.localeCompare(b.nome, "pt-BR")
    );
    return { gerentes: gerentesOrdenados };
}

export async function action({ request }: Route.ActionArgs) {
  const { apiClient } = await getSessionAutenticada(request);
  const formData = await request.formData();
  const cpf = String(formData.get("cpf") ?? "");

  if (!cpf) {
    return data({ formError: "CPF não informado." }, { status: 400 });
  }

  const response = await apiClient.delete(`/gerentes/${cpf}`);

  if (!response.ok) {
    return data({ formError: "Não foi possível remover o gerente." }, { status: response.status });
  }

  return redirect("/admin/gerentes");
}


export default function ListarGerentes({ loaderData }: Route.ComponentProps) {
    const { gerentes } = loaderData;


    return (
        <div className="flex flex-col gap-6">
            <AppBreadcrumb
                items={[
                    { label: "Home", href: "/" },
                    { label: "Gerentes" },
                ]}
            />
            <div className="flex justify-between">
                <h1 className="text-sm text-primary uppercase flex items-center gap-2">
                    Gerentes
                </h1>

                <NavLink to={"/admin/gerentes/novo"}>
                    <Button
                        variant="confirm"
                        className="h-9 px-4 text-xs uppercase sm:w-auto">
                        Novo Gerente
                    </Button>
                </NavLink>
            </div>


            <div className="flex flex-col bg-card border-3 border-border p-4 gap-4">
                <div className="border-b border-border pb-4">
                    <span className="text-primary text-xs uppercase flex items-center gap-2">
                        <LayoutDashboard size={16} /> LISTA DE GERENTES
                    </span>
                </div>

                <TabelaAdminListarGerentes clientes={gerentes} />
            </div>
        </div>
    );
}
