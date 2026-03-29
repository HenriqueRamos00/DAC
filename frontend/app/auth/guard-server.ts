import { redirect } from "react-router";
import { getSession } from "./sessions.server";
import { getAllowedRoles, roleMapping } from "./permissions";
import { sanitizeAuth } from "~/models/auth/AuthUser";

/**
 * Verifica se o usuário tem permissão para acessar a rota atual.
 *
 * - Sem sessão -> redireciona pro /login
 * - Role errada -> redireciona pro dashboard da role do usuário
 * - Role correta -> passa
 */
export async function enforcePermissions(request: Request) {
  const url = new URL(request.url);
  const allowed = getAllowedRoles(url.pathname);

  if (!allowed) return;

  const session = await getSession(request.headers.get("Cookie"));
  const auth = sanitizeAuth(session);

  if (!auth.token || !auth.tipo) {
    throw redirect("/login");
  }

  const role = roleMapping[auth.tipo];

  if (!role || !allowed.includes(role)) {
    throw redirect(`/${role ?? "/"}`);
  }
}
