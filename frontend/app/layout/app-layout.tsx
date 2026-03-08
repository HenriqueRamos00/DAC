import { AppSidebar } from "~/components/app-sidebar";
import { SidebarProvider, SidebarTrigger } from "~/components/ui/sidebar";
import { Outlet } from "react-router";
import { initSessionStorage } from "~/auth/session-server";
import { enforcePermissions } from "~/auth/guard-server";
import type { Route } from "./+types/app-layout";

/* export async function loader({ request, context }: Route.LoaderArgs) {
  initSessionStorage(context.cloudflare.env.SESSION_SECRET);
  const auth = await enforcePermissions(request);
  return { role: auth?.role ?? null };
} */

export async function loader({ request, context }: Route.LoaderArgs) {
  // TODO: remover quando login estiver pronto
  return { role: "cliente" as const }; // troca pra "cliente" ou "gerente" ou "admin" pra testar
}

export default function Layout({ loaderData }: Route.ComponentProps) {
  return (
    <SidebarProvider>
      <AppSidebar role={loaderData.role} />
      <main>
        <SidebarTrigger />
        <Outlet />
      </main>
    </SidebarProvider>
  );
}
