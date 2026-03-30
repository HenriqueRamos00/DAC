import { getSessionAutenticada } from "~/services/auth.server";
import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import type { Cliente } from "~/models/dto/Cliente";
import { TabelaAprovacao } from "~/features/tabela-aprovacao/tabela-aprovacao";
import { useEffect, useState } from "react";
import { DialogAprovarCliente } from "~/components/approv-client";
import { DialogRejeitarCliente } from "~/components/reject-client";
import { data, useFetcher } from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Dashboard Gerente" },
    { name: "description", content: "Painel do gerente" },
  ];
}

export async function loader({ request }:Route.LoaderArgs) {
  const { apiClient } = await getSessionAutenticada(request);
  const params = new URLSearchParams({
    filtro: "para_aprovar"
  })
  const res = await apiClient.get(`/clientes?${params.toString()}`);

  if (!res.ok) {
    throw new Response("Erro ao obter informações do cliente", {status: res.status});
  }

  const clientes = (await res.json()) as Cliente[];
  return { clientes };
}

export async function action({ request }:Route.ActionArgs) {
  const formData = await request.formData();
  const intent = formData.get("intent");
  const cpf = formData.get("cpf");
  const motivo = formData.get("motivo");

  if (typeof intent !== "string" || typeof cpf !== "string"){
    return data(
      { ok: false, error: "Dados inválidos" }, 
      { status: 400 }
    );
  }

  const { apiClient } = await getSessionAutenticada(request);

  let res: Response;
  if (intent === "aprovar"){
    res = await apiClient.post(`/clientes/${cpf}/aprovar`);
  } else if (intent === "rejeitar") {
    const motivoStr = typeof motivo === "string" ? motivo.trim() : "";
    res = await apiClient.post(`/clientes/${cpf}/rejeitar`, 
      {motivo: motivoStr} 
    );
  } else {
    return data(
      { ok: false, error: "Ação inválida" }, 
      { status: 400 }
    );
  }

  if (!res.ok) {
    return data(
      { ok: false, error: "Falha ao processar solicitação" }, 
      { status: res.status }
    );
  }

  return {ok: true};
}

export default function GerenteDashboard({ loaderData } : Route.ComponentProps) {
  const { clientes } = loaderData;

  const CLOSE_ANIM_MS = 100;
  const [clienteSelecionado, setClienteSelecionado] = useState<Cliente | null>(null);
  const [aprovarOpen, setAprovarOpen] = useState(false);
  const [rejeitarOpen, setRejeitarOpen] = useState(false);

  const fetcher = useFetcher<{ ok: boolean; error?: string }>();
  const isSubmitting = fetcher.state !== "idle";

  useEffect(() => {
    if (fetcher.state === "idle" && fetcher.data?.ok) {
      setAprovarOpen(false);
      setRejeitarOpen(false);
      setTimeout(() => setClienteSelecionado(null), CLOSE_ANIM_MS)
    }
  }, [fetcher.state, fetcher.data])

  function abrirAprovar(cliente: Cliente) {
    setClienteSelecionado(cliente);
    setAprovarOpen(true);
  }

  function confirmarAprovar() {
    if (!clienteSelecionado) return;
    fetcher.submit({
      intent: "aprovar",
      cpf: clienteSelecionado.cpf
    }, { method: "POST" });
  }

  function abrirRejeitar(cliente: Cliente) {
    setClienteSelecionado(cliente);
    setRejeitarOpen(true);
  }

  function confirmarRejeitar(motivo: string) {
    if (!clienteSelecionado) return;
    fetcher.submit({
      intent: "rejeitar",
      cpf: clienteSelecionado.cpf,
      motivo
    }, { method: "POST" })
  }

  return (
    <div className="flex flex-col gap-6">
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Gerente" },
        ]}
      />
      <div className="flex flex-col bg-card border-3 border-border p-4 gap-4"> 
        <div className="border-b border-border pb-4"> 
          <span className="text-primary text-xs uppercase flex items-center gap-2"> Solicitações de Cadastro </span> 
        </div> 
            <TabelaAprovacao 
              clientes={clientes} 
              onAprovar={abrirAprovar} 
              onRejeitar={abrirRejeitar}
            /> 
      </div>

      <DialogAprovarCliente 
        open={aprovarOpen}
        cliente={clienteSelecionado}
        onOpenChange={setAprovarOpen}
        onConfirm={confirmarAprovar}
        isLoading={isSubmitting}
      />

      <DialogRejeitarCliente
        open={rejeitarOpen}
        cliente={clienteSelecionado}
        onOpenChange={setRejeitarOpen}
        onConfirm={confirmarRejeitar}
        isLoading={isSubmitting}
      />
    </div>
  );
}
