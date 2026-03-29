import { AppSidebar } from "~/components/app-sidebar";
import { SidebarProvider } from "~/components/ui/sidebar";
import { Outlet } from "react-router";
import type { Route } from "./+types/app-layout";
import AppNav from "~/components/app-nav";
import { useAuth } from "~/components/auth-provider";
import { roleMapping } from "~/auth/permissions";
import { enforcePermissions } from "~/auth/guard-server";

export async function loader({ request }: Route.LoaderArgs) {
  await enforcePermissions(request);
  return null;
}

export default function Layout() {
  const { auth } = useAuth();
  const role = roleMapping[auth.tipo ?? ""] ?? null;

  return (
    <SidebarProvider>
      <AppSidebar role={role} />
      <div className="flex flex-col flex-1">
        <AppNav />
        <main className="flex-1 p-10 md:px-8 md:py-6">
          <Outlet />
        </main>
      </div>
    </SidebarProvider>
  );
}
