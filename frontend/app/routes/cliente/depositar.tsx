import type { Route } from "./+types/dashboard";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Depositar" },
    { name: "description", content: "Depositar" },
  ];
}

export default function Depositar() {
  return (
    <div>
      Depositar
    </div>
  )
}
