import type { Route } from "./+types/dashboard";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Extrato" },
    { name: "description", content: "Extrato" },
  ];
}

export default function Extrato() {
  return (
    <div>
      Extrato
    </div>
  )
}
