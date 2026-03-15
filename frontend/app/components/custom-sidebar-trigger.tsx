import { Menu, X } from "lucide-react";
import { useSidebar } from "~/components/ui/sidebar";

export default function CustomSidebarTrigger() {
  const { toggleSidebar, open } = useSidebar();
  const Icon = open ? X : Menu;

  return (
    <button
      type="button"
      onClick={toggleSidebar}
      aria-label={open ? "Fechar menu lateral" : "Abrir menu lateral"}
      className="inline-flex size-8 items-center justify-center rounded-md hover:bg-sidebar-accent/20 transition-colors"
    >
      <Icon size={18} />
    </button>
  );
}
