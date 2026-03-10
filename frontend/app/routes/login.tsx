import { Link } from "react-router";
import { CrtMonitor } from "~/components/crt-monitor";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "~/components/ui/card";
import { Input } from "~/components/ui/input";
import { Button } from "~/components/ui/button";

export default function Login() {
  return (
    <div className="crt-page">
      <CrtMonitor title="">
        <Card className="w-full max-w-sm pointer-events-auto">
          <CardHeader className="items-center">
            <CardTitle className="text-center text-base text-primary retro-glow">LOGIN</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col gap-4">
            <div className="flex flex-col gap-1">
              <label htmlFor="email" className="text-sm font-medium">Email</label>
              <Input id="email" type="email" placeholder="joao@retrobank.com" />
            </div>
            <div className="flex flex-col gap-1">
              <label htmlFor="senha" className="text-sm font-medium">Senha</label>
              <Input id="senha" type="password" placeholder="••••••••" />
            </div>
            <Link to="/cliente" className="w-full cursor-pointer hover:opacity-90 transition-opacity">
              <Button className="w-full hover:bg-primary/80">Entrar</Button>
            </Link>
          </CardContent>
          <CardFooter className="flex flex-col gap-2 items-center">
            <Link to="/cadastro" className="text-sm text-muted-foreground underline hover:text-foreground">
              Criar nova conta
            </Link>
            <Link to="/" className="text-sm text-muted-foreground underline hover:text-foreground">
              Voltar ao início
            </Link>
          </CardFooter>
        </Card>
      </CrtMonitor>
    </div>
  );
}
