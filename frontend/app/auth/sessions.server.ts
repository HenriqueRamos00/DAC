import { env } from "cloudflare:workers";
import { createCookieSessionStorage } from "react-router";

export interface SessionBase {
  token: string;
  nome: string;
  email: string;
  cpf: string;
}

export interface ClientSession extends SessionBase {
  tipo: "CLIENTE";
  conta: string;
}

export interface StaffSession extends SessionBase {
  tipo: "GERENTE" | "ADMINISTRADOR";
  conta?: never;
}

export type SessionData = ClientSession | StaffSession;

const sessionStorage = createCookieSessionStorage<SessionData>({
  cookie: {
    name: "__session",
    httpOnly: true,
    secure: env.NODE_ENV === "production",
    sameSite: "lax",
    path: "/",
    maxAge: 60 * 60 * 24,
    //secrets: [env.SESSION_SECRET]
  },
});

export const { getSession, commitSession, destroySession } = sessionStorage;