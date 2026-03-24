import type { LucideIcon } from "lucide-react";
import { cn } from "~/lib/utils";

interface PainelProps {
  icon: LucideIcon;
  title: string;
  content: string;
  className?: string;
}

export default function Painel({ icon: Icon, content, title, className }: PainelProps) {
  return (
    <div className={cn("flex flex-col bg-sidebar border-6 p-3 gap-3 text-ring", className)}>
      <div className="flex items-center gap-2">
        <Icon size={16} />
        <p className="text-xs uppercase font-mono font-bold">{title}</p>
      </div>
      <p className="text-sm">{content}</p>
    </div>
  );
}
