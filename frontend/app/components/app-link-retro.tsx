import type { LucideIcon } from "lucide-react";
import { Link } from "react-router";
import { cn } from "~/lib/utils";

interface LinkRetroProps {
  icon: LucideIcon;
  title: string;
  link: string;
  className?: string;
}

export default function LinkRetro({ icon: Icon, title, link, className }: LinkRetroProps) {
  return (
    <Link to={link} className={cn("flex flex-col items-center hover:pixel-border-glow bg-sidebar border-6 p-3 gap-3 text-ring", className)}>
      <Icon size={20} />
      <p className="text-xs font-mono font-bold">{title}</p>
    </Link>
  );
}
