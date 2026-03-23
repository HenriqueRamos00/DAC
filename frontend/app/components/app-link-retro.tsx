import type { LucideIcon } from "lucide-react";
import { Link } from "react-router";

interface LinkRetroProps {
  icon: LucideIcon;
  title: string;
  link: string
  /**
   * Usar classes do tailwind
   */
  color: string;
}

export default function LinkRetro({ icon: Icon, title, link, color }: LinkRetroProps) {
  return (
    <Link to={link} className="flex flex-col items-center hover:pixel-border-glow bg-sidebar border-6 p-3 gap-3 text-ring">
      <Icon size={20} className={`${color}`} />
      <p className="text-xs font-mono font-bold">{title}</p>
    </Link>
  );
}
