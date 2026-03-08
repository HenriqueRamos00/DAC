import { redirect } from "react-router";
import { getRole, getToken } from "./session-server";
import { getAllowedRoles, type AppRole } from "./permissions";

interface AuthContext {
  role: AppRole;
  token: string;
}

/**
 * Verifica permissões e retorna o contexto de auth pra rotas protegidas.
 *
 * - Rota pública -> retorna undefined (sem exigência de login)
 * - Rota protegida sem sessão -> redireciona pro /login
 * - Rota protegida com role errada -> redireciona pro dashboard da role do usuário
 * - Rota protegida com role correta -> retorna { role, token }
 *
 * O token retornado pode ser usado nos loaders pra chamar o Spring:
 * fetch(url, { headers: { Authorization: `Bearer ${auth.token}` } })
 */
export async function enforcePermissions(request: Request): Promise<AuthContext | undefined> {
  const url = new URL(request.url);
  const allowed = getAllowedRoles(url.pathname);

  // rota pública - não exige auth
  if (!allowed) return undefined;

  const [role, token] = await Promise.all([
    getRole(request),
    getToken(request),
  ]);

  // TODO: substituir por `throw redirect("/login")` quando a rota estiver implementada
  if (!role || !token) {
    return undefined;
  }

  if (!allowed.includes(role)) {
    throw redirect(`/${role}`);
  }

  return { role, token };
}