import type { LucideIcon } from "lucide-react";

interface PainelProps {
  icon: LucideIcon;
  title: string;
  content: string;
  color: string;
  variant?: 'note';
}

export default function Painel({ icon: Icon, content, title, color, variant }: PainelProps) {
  return (
    <div className="flex flex-col bg-sidebar border-6 p-3 gap-3 text-ring">
      <div className="flex items-center gap-2">
        <Icon size={16} />
        <p className="text-xs uppercase font-mono font-bold">{title}</p>
      </div>
      <p className={`text-sm ${color}`}>{content}</p>
    </div>
  );
}
