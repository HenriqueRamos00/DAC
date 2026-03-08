import {
  ArrowDownToLine,
  ArrowLeftRight,
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
  cliente: {
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
    color: "text-[var(--manager)]",
    items: [
      { label: "Dashboard", path: "/gerente", icon: LayoutDashboard },
      { label: "Todos os Clientes", path: "/gerente/clientes", icon: Users },
      { label: "Consultar Cliente", path: "/gerente/consultar", icon: UserCheck },
      { label: "Top 3 Clientes", path: "/gerente/top3", icon: Trophy },
    ],
  },
  admin: {
    color: "text-[var(--admin)]",
    items: [
      { label: "Dashboard", path: "/admin", icon: LayoutDashboard },
      { label: "Relatório Clientes", path: "/admin/relatorio", icon: ClipboardList },
      { label: "Gerentes", path: "/admin/gerentes", icon: Users },
      { label: "Novo Gerente", path: "/admin/gerentes/novo", icon: UserPlus },
    ],
  },
};

export function AppSidebar({ role="cliente" }: SidebarProps) {
  const { color, items } = sidebarItems[role]
  const { pathname } = useLocation()

  return (
    <Sidebar>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem className="flex flex-col gap-4 p-4 border-b border-sidebar-border">
              <NavLink to="/" className="text-base text-primary retro-glow hover:brightness-125 transition">
                Retro-bank
              </NavLink>
            <div className={`flex text-sm ${color}`}>
              ► {role}
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
