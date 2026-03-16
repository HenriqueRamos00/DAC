import { data, Form, Link, redirect, useActionData } from "react-router";
import { CrtMonitor } from "~/components/crt-monitor";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "~/components/ui/card";
import { Input } from "~/components/ui/input";
import { Button } from "~/components/ui/button";
import type { Route } from "../+types/root";
import { authCookie, sessionCookie } from "~/auth/cookie";
import { api } from "~/services/api.server";

export async function loader({request} : Route.LoaderArgs) {
  const token = await authCookie.parse(request.headers.get("Cookie"))

  if (token) {
    const session = await sessionCookie.parse(request.headers.get("Cookie"))

    switch (session?.tipo) {
      case "CLIENTE":
        return redirect("/cliente");
      case "GERENTE":
        return redirect("/gerente");
      case "ADMIN":
        return redirect("/admin");
      default:
        return redirect("/");
    }
  }
  return null;
}

export async function action({request} : Route.ActionArgs) {
  const formData = await request.formData();
  const email = formData.get("email");
  const senha = formData.get("senha");

  if (!email || !senha) {
    return data(
      { error: "Email e Senha Obrigatórios" },
      { status: 400 }
    )
  }

  const apiClient = api(request);
  const res = await apiClient.post("/login", {login: email, senha});

    if (!res.ok) {
    const body = await res.json().catch(() => null);
    return data(
      { error: "Email ou senha inválidos." },
      { status: 401 }
    );
  }

  const { access_token, tipo, usuario } = (await res.json()) as {
    access_token: string;
    tipo: string;
    usuario: { nome: string; email: string; cpf: string };
  };

  let redirectTo = "/";
  switch (tipo) {
    case "CLIENTE":
      redirectTo = "/cliente";
      break;
    case "GERENTE":
      redirectTo = "/gerente";
      break;
    case "ADMIN":
      redirectTo = "/admin";
      break;
  }

  return redirect(redirectTo, {
    headers: [
      ["Set-Cookie", await authCookie.serialize(access_token)],
      ["Set-Cookie", await sessionCookie.serialize({
        tipo,
        nome: usuario.nome,
        email: usuario.email,
        cpf: usuario.cpf,
      })],
    ],
  });
}

export default function Login({actionData} : Route.ComponentProps) {
  return (
    <div className="crt-page">
      <CrtMonitor title="">
        <Card className="w-full max-w-sm pointer-events-auto">
          <CardHeader className="items-center">
            <CardTitle className="text-center text-base text-primary retro-glow">LOGIN</CardTitle>
          </CardHeader>
          <Form method="post" viewTransition>
            <CardContent className="flex flex-col gap-4">
              <div className="flex flex-col gap-1">
                <label htmlFor="email" className="text-sm font-medium">Email</label>
                <Input id="email" name="email" type="email" placeholder="joao@retrobank.com" />
              </div>
              <div className="flex flex-col gap-1">
                <label htmlFor="senha" className="text-sm font-medium">Senha</label>
                <Input id="senha" name="senha" type="password" placeholder="••••••••" />
              </div>
              <Button type="submit" className="w-full hover:bg-primary/80">Entrar</Button>
            </CardContent>
          </Form>
          <CardFooter className="flex flex-col gap-2 items-center">
            <Link to="/cadastro"
                  viewTransition
                  className="text-sm text-muted-foreground underline hover:text-foreground">
              Criar nova conta
            </Link>
            <Link to="/" 
                  viewTransition
                  className="text-sm text-muted-foreground underline hover:text-foreground">
              Voltar ao início
            </Link>
          </CardFooter>
        </Card>
      </CrtMonitor>
    </div>
  );
}
