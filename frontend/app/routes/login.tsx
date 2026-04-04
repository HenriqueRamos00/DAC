import { data, Form, Link, redirect, useActionData } from "react-router";
import { CrtMonitor } from "~/components/crt-monitor";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "~/components/ui/card";
import { Input } from "~/components/ui/input";
import { Button } from "~/components/ui/button";
import type { Route } from "./+types/login";
import { api } from "~/services/api.server";
import { commitSession, getSession } from "~/auth/sessions.server";
import type { Cliente } from "~/models/dto/Cliente";

export async function loader({request} : Route.LoaderArgs) {
  const session = await getSession(request.headers.get("Cookie"));
  const token = session.get("token");
  const tipo = session.get("tipo");

  if (token) {
    switch (tipo) {
      case "CLIENTE":
        return redirect("/cliente");
      case "GERENTE":
        return redirect("/gerente");
      case "ADMINISTRADOR":
        return redirect("/admin");
      default:
        return redirect("/");
    }
  }
}

export async function action({request} : Route.ActionArgs) {
  const formData = await request.formData();
  const email = formData.get("email");
  const senha = formData.get("senha");

  if (!email || !senha) {
    return data({ error: "Email e Senha Obrigatórios" }, { status: 400 });
  }

  const apiClient = api(request);
  const res = await apiClient.post("/login", { login: email, senha });

  if (!res.ok) {
    return data({ error: "Email ou senha inválidos." }, { status: 401 });
  }

  const { access_token, tipo, usuario } = (await res.json()) as {
    access_token: string;
    tipo: "CLIENTE" | "GERENTE" | "ADMINISTRADOR";
    usuario: { nome: string; email: string; cpf: string };
  };

  const session = await getSession(request.headers.get("Cookie"));
  session.set("token", access_token);
  session.set("tipo", tipo);
  session.set("nome", usuario.nome);
  session.set("email", usuario.email);
  session.set("cpf", usuario.cpf);
  if (tipo === "CLIENTE") {
    const res_cliente = await apiClient.get(`/clientes/${usuario.cpf}`, {
      headers: { Authorization: `Bearer ${access_token}` }
    })
    const cliente = await res_cliente.json() as Cliente;
    session.set("conta", cliente.conta);
  }

  let redirectTo = "/";
  switch (tipo) {
    case "CLIENTE":
      redirectTo = "/cliente";
      break;
    case "GERENTE":
      redirectTo = "/gerente";
      break;
    case "ADMINISTRADOR":
      redirectTo = "/admin";
      break;
  }

  return redirect(redirectTo, {
    headers: {
      "Set-Cookie": await commitSession(session),
    },
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

              {actionData?.error ? (
                        <p id="saque-error" className="text-sm text-destructive">
                            {actionData.error}
                        </p>
                    ) : null}
                    
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
