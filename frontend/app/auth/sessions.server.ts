import { env } from "cloudflare:workers";
import { createCookieSessionStorage } from "react-router";

export interface SessionData {
  token: string;
  tipo: "CLIENTE" | "GERENTE" | "ADMIN";
  nome: string;
  email: string;
  cpf: string;
}

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