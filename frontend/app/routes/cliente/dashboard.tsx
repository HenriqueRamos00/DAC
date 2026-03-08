import type { Route } from "./+types/dashboard";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Dashboard" },
    { name: "description", content: "Tela inicial do cliente" },
  ];
}

export default function Dashboard() {
  return <h2>Tela inicial</h2>
}
