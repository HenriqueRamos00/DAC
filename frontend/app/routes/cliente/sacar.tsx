import type { Route } from "./+types/dashboard";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Sacar" },
    { name: "description", content: "Sacar" },
  ];
}

export default function Sacar() {
  return (
    <div>
      Sacar
    </div>
  )
}
