import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Dashboard Admin" },
    { name: "description", content: "Painel do administrador" },
  ];
}

export default function AdminDashboard() {
  return (
    <div className="flex flex-col gap-6">
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Administrador" },
        ]}
      />
      <h1 className="text-sm text-primary uppercase">PAINEL DO ADMINISTRADOR</h1>
    </div>
  );
}
