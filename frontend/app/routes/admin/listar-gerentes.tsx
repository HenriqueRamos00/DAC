import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { getSessionAutenticada } from "~/services/auth.server";
import { LayoutDashboard} from "lucide-react";
import { TabelaAdminListarGerentes } from "~/features/tabela-admin-gerentes/tabela-listar-gerentes";
import type { Gerente } from "~/models/dto/Gerente";
import type { Route } from "./+types/listar-gerentes";
import { Button } from "~/components/ui/button";
import { data, NavLink, redirect, useActionData } from "react-router";

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

async function getDeleteErrorMessage(response: Response) {
  try {
    const body = await response.json() as { motivo?: string; message?: string; error?: string };

    if (body.motivo === "ULTIMO_GERENTE") {
      return "Não é possível remover o último gerente do banco.";
    }

    return body.message || body.error || "Não foi possível remover o gerente.";
  } catch {
    return "Não foi possível remover o gerente.";
  }
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
    return data(
      { formError: await getDeleteErrorMessage(response) },
      { status: response.status },
    );
  }

  return redirect("/admin/gerentes");
}


export default function ListarGerentes({ loaderData }: Route.ComponentProps) {
    const { gerentes } = loaderData;
    const actionData = useActionData<typeof action>();


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

                {actionData?.formError ? (
                    <p className="border border-destructive bg-destructive/10 p-3 text-sm text-destructive">
                        {actionData.formError}
                    </p>
                ) : null}

                <TabelaAdminListarGerentes clientes={gerentes} />
            </div>
        </div>
    );
}
