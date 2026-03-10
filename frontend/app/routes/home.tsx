import type { Route } from "./+types/home";
import { CrtMonitor } from "~/components/crt-monitor";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Retro-bank" },
    { name: "description", content: "Seu banco com power up de nostalgia" },
  ];
}

export default function Home() {
  return (
    <div className="crt-page">
      <CrtMonitor
        title="SEU BANCO COM"
        subtitle="power up de nostalgia"
        buttons={[
          {
            label: "LOGIN",
            fillColor: "oklch(0.5386 0.1812 152.33)",
            to: "/login",
          },
          {
            label: "CADASTRO",
            fillColor: "oklch(0.5148 0.214 260.38)",
            to: "/cadastro",
          },
        ]}
        footer="SISTEMA ONLINE"
        footerColor="#FFB800"
      />
    </div>
  );
}
