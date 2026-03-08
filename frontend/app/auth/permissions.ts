export type AppRole = "cliente" | "gerente" | "admin";

interface RoutePermission {
  prefix: string;
  roles: AppRole[];
}

/**
 * Mapa de prefixo de rota -> roles permitidas.
 * Qualquer rota que comece com o prefixo exige uma das roles listadas.
 * Rotas sem match são públicas.
 */
const routePermissions: RoutePermission[] = [
  { prefix: "/cliente", roles: ["cliente"] },
  { prefix: "/gerente", roles: ["gerente"] },
  { prefix: "/admin", roles: ["admin"] },
];

export function getAllowedRoles(pathname: string): AppRole[] | undefined {
  const match = routePermissions.find((r) => pathname.startsWith(r.prefix));
  return match?.roles;
}