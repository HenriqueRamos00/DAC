import { env } from "cloudflare:workers";
import { createCookie } from "react-router";

export interface SessionData {
  tipo: "CLIENTE" | "GERENTE" | "ADMIN";
  nome: string;
  email: string;
  cpf: string;
}

export const authCookie = createCookie("token", {
    httpOnly: true,
    secure: env.NODE_ENV === "production",
    sameSite: "lax",
    path: "/",
    maxAge: 60 * 60 * 24
})

export const sessionCookie = createCookie("session", {
  httpOnly: false,
  secure: env.NODE_ENV === "production",
  sameSite: "lax",
  path: "/",
  maxAge: 60 * 60 * 24
});