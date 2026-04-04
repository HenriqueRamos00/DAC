import {
  ArrowDownToLine,
  ArrowLeftRight,
  ArrowUpFromLine,
  ChevronRight,
  ClipboardList,
  FileText,
  LayoutDashboard,
  LogOut,
  Trophy,
  User,
  UserCheck,
  UserPlus,
  Users
} from "lucide-react"
import { Form, NavLink, useLocation } from "react-router"
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuItem,
} from "~/components/ui/sidebar"
import type { AppRole } from "~/auth/permissions"

interface SidebarProps {
  role: AppRole | null;
}

const sidebarItems = {
  cliente: {
    label: "Cliente",
    color: "text-[var(--customer)]",
    items: [
      { label: "Dashboard", path: "/cliente", icon: LayoutDashboard },
      { label: "Meu Perfil", path: "/cliente/perfil", icon: User },
      { label: "Depositar", path: "/cliente/deposito", icon: ArrowDownToLine },
      { label: "Sacar", path: "/cliente/saque", icon: ArrowUpFromLine },
      { label: "Transferir", path: "/cliente/transferencia", icon: ArrowLeftRight },
      { label: "Extrato", path: "/cliente/extrato", icon: FileText },
    ],
  },
  gerente: {
    label: "Gerente",
    color: "text-[var(--manager)]",
    items: [
      { label: "Dashboard", path: "/gerente", icon: LayoutDashboard },
      { label: "Todos os Clientes", path: "/gerente/clientes", icon: Users },
      { label: "Consultar Cliente", path: "/gerente/consultar", icon: UserCheck },
      { label: "Top 3 Clientes", path: "/gerente/top3", icon: Trophy },
    ],
  },
  administrador: {
    label: "Administrador",
    color: "text-[var(--admin)]",
    items: [
      { label: "Dashboard", path: "/admin", icon: LayoutDashboard },
      { label: "Relatório Clientes", path: "/admin/relatorio-clientes", icon: ClipboardList },
      { label: "Gerentes", path: "/admin/gerentes", icon: Users },
      { label: "Novo Gerente", path: "/admin/gerentes/novo", icon: UserPlus },
    ],
  },
};

export function AppSidebar({ role }: SidebarProps) {
  if (!role) return null;

  const { label, color, items } = sidebarItems[role]
  const { pathname } = useLocation()

  return (
    <Sidebar>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem className="flex flex-col gap-4 p-4 border-b border-sidebar-border">
              <NavLink to="/" className="text-base text-primary retro-glow hover:brightness-125 transition">
                Retro-bank
              </NavLink>
            <div className={`flex items-center gap-1 text-xs ${color}`}>
              <ChevronRight size={16} />{label}
            </div>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup />
          <SidebarMenu>
            {
              items.map((link) => {
                  const linkAtivo = Boolean(pathname === link.path)
                  const classeBase = "flex items-center gap-3 pl-5 py-3 text-sm text-green-200 "
                  const classAtivo = linkAtivo ? "text-sidebar-primary border-l-2 border-sidebar-primary bg-sidebar-accent/2"
                                              : "hover:bg-sidebar-accent/2 border-l-2 border-transparent"
                  return (
                    <SidebarMenuItem key={link.path}>
                      <NavLink to={link.path} className={ classeBase.concat(classAtivo)}>
                        <link.icon size={16}/>
                        <span>{link.label}</span>
                      </NavLink>
                    </SidebarMenuItem>
                  )
              })}
          </SidebarMenu>
        <SidebarGroup />
      </SidebarContent>

      <SidebarFooter>
        <SidebarMenu>
          <SidebarMenuItem className="border-t border-sidebar-border" >
            <Form method="post" action="/logout" viewTransition>
              <button
                type="submit"
                className="flex items-center text-green-200 text-sm pl-5 gap-3 py-3 hover:bg-sidebar-accent/2 w-full"
              >
                <LogOut size={16} />
                <span>Sair</span>
              </button>
            </Form>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  )
}
