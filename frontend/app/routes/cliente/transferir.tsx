import type { Route } from "./+types/dashboard";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Transferir" },
    { name: "description", content: "Transferir" },
  ];
}

export default function Transferir() {
  return (
    <div>
      Transferir
    </div>
  )
}
