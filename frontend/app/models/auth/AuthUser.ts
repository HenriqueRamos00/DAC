import type { Session } from "react-router"

export type AuthUser = {
    token: string | null
    tipo: "CLIENTE" | "GERENTE" | "ADMIN" | null
    nome: string | null
    email: string | null
    cpf: string | null
}

export function sanitizeAuth(session : Session) : AuthUser{
  return {
    token: typeof session.get("token") === "string" ? session.get("token") : null,
    tipo: typeof session.get("tipo") === "string" ? session.get("tipo") : null,
    nome: typeof session.get("nome") === "string" ? session.get("nome") : null,
    email: typeof session.get("email") === "string" ? session.get("email") : null,
    cpf: typeof session.get("cpf") === "string" ? session.get("cpf") : null,
  }
}