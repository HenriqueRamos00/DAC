import {
  ArrowDownToLine,
  ArrowLeftRight,
  ArrowRight,
  ArrowUpFromLine,
  ClipboardList,
  FileText,
  LayoutDashboard,
  Trophy,
  User,
  UserCheck,
  UserPlus,
  Users
} from "lucide-react"
import type { ReactNode } from "react";
import { NavLink, useLocation } from "react-router"
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "~/components/ui/sidebar"

interface SidebarProps {
  role?: "cliente" | "gerente" | "admin"
}

const sidebarItems = {
  cliente: [
    { label: "Dashboard", path: "/cliente", icon: LayoutDashboard },
    { label: "Meu Perfil", path: "/cliente/perfil", icon: User },
    { label: "Depositar", path: "/cliente/deposito", icon: ArrowDownToLine },
    { label: "Sacar", path: "/cliente/saque", icon: ArrowUpFromLine },
    { label: "Transferir", path: "/cliente/transferencia", icon: ArrowLeftRight },
    { label: "Extrato", path: "/cliente/extrato", icon: FileText },
  ],
  gerente: [
    { label: "Dashboard", path: "/gerente", icon: LayoutDashboard },
    { label: "Todos os Clientes", path: "/gerente/clientes", icon: Users },
    { label: "Consultar Cliente", path: "/gerente/consultar", icon: UserCheck },
    { label: "Top 3 Clientes", path: "/gerente/top3", icon: Trophy },
  ],
  admin: [
    { label: "Dashboard", path: "/admin", icon: LayoutDashboard },
    { label: "Relatório Clientes", path: "/admin/relatorio", icon: ClipboardList },
    { label: "Gerentes", path: "/admin/gerentes", icon: Users },
    { label: "Novo Gerente", path: "/admin/gerentes/novo", icon: UserPlus },
  ],
};

export function AppSidebar({ role="cliente" }: SidebarProps) {
  const options = sidebarItems[role]
  const { pathname } = useLocation()

  return (
    <Sidebar>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem className="flex flex-col gap-4 p-4 border-b border-sidebar-border">
              <NavLink to="/" className="font-pixel text-[10px] text-primary retro-glow drop-shadow-lg drop-shadow-primary hover:brightness-110 transition">
                Retro-bank
              </NavLink>
            <div className="flex text-[10px] font-pixel items-center">
              <ArrowRight size="20px"/>{role}
            </div>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup />
          <SidebarMenu>
            {
              options.map((link) => {
                  const linkAtivo = Boolean(pathname === link.path)
                  const classeBase = "font-pixel retro glow flex items-center gap-3 pl-4 pb-2 text-[10px] text-green-200 "
                  const classAtivo = linkAtivo ? "text-sidebar-primary border-l-2 border-sidebar-primary"
                                              : "hover:bg-sidebar-accent/10 border-l-2 border-transparent"
                  return (
                    <SidebarMenuItem>
                      <NavLink key={link.path} to={link.path} className={ classeBase.concat(classAtivo)}>
                        <link.icon size={16}/>
                        <span className="text-xs">{link.label}</span>
                      </NavLink>
                    </SidebarMenuItem>
                  )
              })}
          </SidebarMenu>
        <SidebarGroup />
      </SidebarContent>

      <SidebarFooter>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton>
              <User /> Username
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  )
}
