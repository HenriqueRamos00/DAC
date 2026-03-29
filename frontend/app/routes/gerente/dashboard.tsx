import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Dashboard Gerente" },
    { name: "description", content: "Painel do gerente" },
  ];
}

export default function GerenteDashboard() {
  return (
    <div className="flex flex-col gap-6">
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Gerente" },
        ]}
      />
      <h1 className="text-sm text-primary uppercase">PAINEL DO GERENTE</h1>
    </div>
  );
}
