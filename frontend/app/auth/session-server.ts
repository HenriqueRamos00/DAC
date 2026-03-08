import { createCookieSessionStorage } from "react-router";
import type { AppRole } from "./permissions";

/**
 * Payload futuro da JWT que o Spring Boot gera. 
 * O BFF só repassa o token, então a validação e extração de claims é responsabilidade do backend.
 */
export interface JwtPayload {
  sub: string;
  role: string;
  exp: number;
  iat: number;
}

let sessionStorage: ReturnType<typeof createCookieSessionStorage<{ token: string }>>;

export function initSessionStorage(secret: string) {
  if (sessionStorage) return;
  sessionStorage = createCookieSessionStorage<{ token: string }>({
    cookie: {
      name: "auth_token",
      secrets: [secret],
      sameSite: "lax",
      httpOnly: true,
      secure: true,
      path: "/",
      maxAge: 60 * 60 * 24,
    },
  });
}

/**
 * Decodifica o payload de um JWT sem verificar assinatura.
 * A verificação é responsabilidade do Spring Boot quando o BFF repassa o token.
 */
function decodeJwtPayload(token: string): JwtPayload {
  const base64Payload = token.split(".")[1];
  const json = atob(base64Payload);
  return JSON.parse(json) as JwtPayload;
}

/**
 * Mapeia a role vinda do JWT pro tipo do frontend.
 */
const roleMap: Record<string, AppRole> = {
  CLIENTE: "cliente",
  GERENTE: "gerente",
  ADMIN: "admin",
};

function mapRole(jwtRole: string): AppRole | undefined {
  return roleMap[jwtRole.toUpperCase()];
}

// --- Funções públicas ---

export async function getToken(request: Request): Promise<string | undefined> {
  const session = await sessionStorage.getSession(request.headers.get("Cookie"));
  return session.get("token") as string | undefined;
}

export async function getRole(request: Request): Promise<AppRole | undefined> {
  const token = await getToken(request);
  if (!token) return undefined;

  const payload = decodeJwtPayload(token);
  return mapRole(payload.role);
}

export async function createSessionWithToken(token: string): Promise<string> {
  const session = await sessionStorage.getSession();
  session.set("token", token);
  return sessionStorage.commitSession(session);
}

export async function destroyAuthSession(request: Request): Promise<string> {
  const session = await sessionStorage.getSession(request.headers.get("Cookie"));
  return sessionStorage.destroySession(session);
}