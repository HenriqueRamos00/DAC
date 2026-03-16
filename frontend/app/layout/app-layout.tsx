import { AppSidebar } from "~/components/app-sidebar";
import { SidebarProvider } from "~/components/ui/sidebar";
import { Outlet } from "react-router";
import type { Route } from "./+types/app-layout";
import AppNav from "~/components/app-nav";

/* export async function loader({ request, context }: Route.LoaderArgs) {
  initSessionStorage(context.cloudflare.env.SESSION_SECRET);
  const auth = await enforcePermissions(request);
  return { role: auth?.role ?? null };
} */

export async function loader({ request, context }: Route.LoaderArgs) {
  await new Promise<void>((resolve) => setTimeout(resolve, 500));
  // TODO: remover quando login estiver pronto
  return { role: "cliente" as const }; // troca pra "cliente" ou "gerente" ou "admin" pra testar
}

export default function Layout({ loaderData }: Route.ComponentProps) {
  return (
    <SidebarProvider>
      <AppSidebar role={loaderData.role} />
      <div className="flex flex-col flex-1">
        <AppNav />
        <main className="flex-1 p-10 md:px-5 md:py-5">
          <Outlet />
        </main>
      </div>
    </SidebarProvider>
  );
}
