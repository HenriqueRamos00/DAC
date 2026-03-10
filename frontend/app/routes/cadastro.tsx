import { Link } from "react-router";
import { CrtMonitor } from "~/components/crt-monitor";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "~/components/ui/card";
import { Input } from "~/components/ui/input";
import { Button } from "~/components/ui/button";

export default function Autocadastro() {
  return (
    <div className="crt-page flex flex-col items-center justify-center font-mono py-8">
      
      {/* Cabeçalho Global (Opcional, caso não esteja no layout principal) */}
      <div className="flex flex-col items-center mb-6">
        <h1 className="text-3xl font-bold text-[#00ff66] retro-glow mb-1" style={{ textShadow: '0 0 10px #00ff66' }}>
          RETRO-BANK
        </h1>
        <p className="text-muted-foreground text-sm tracking-widest">
          ★ NOVO JOGADOR
        </p>
      </div>

      <CrtMonitor title="">
        {/* Card expandido para acomodar duas colunas */}
        <Card className="w-full max-w-2xl pointer-events-auto bg-[#1a1f2e] border-slate-700 text-slate-300 rounded-none">
          <CardHeader className="items-center pb-6">
            <CardTitle className="text-center text-lg text-[#f8f8f2] tracking-wider uppercase">
              Autocadastro
            </CardTitle>
          </CardHeader>

          <CardContent className="flex flex-col gap-5">
            {/* Linha 1 */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col gap-2">
                <label htmlFor="nome" className="text-xs font-bold uppercase text-slate-400">Nome Completo</label>
                <Input id="nome" type="text" placeholder="João da Silva" className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600" />
              </div>
              <div className="flex flex-col gap-2">
                <label htmlFor="cpf" className="text-xs font-bold uppercase text-slate-400">CPF</label>
                <Input id="cpf" type="text" placeholder="000.000.000-00" className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600" />
              </div>
            </div>

            {/* Linha 2 */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col gap-2">
                <label htmlFor="email" className="text-xs font-bold uppercase text-slate-400">E-mail</label>
                <Input id="email" type="email" placeholder="joao@email.com" className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600" />
              </div>
              <div className="flex flex-col gap-2">
                <label htmlFor="telefone" className="text-xs font-bold uppercase text-slate-400">Telefone</label>
                <Input id="telefone" type="text" placeholder="(00) 00000-0000" className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600" />
              </div>
            </div>

            {/* Linha 3 */}
            <div className="flex flex-col gap-2">
              <label htmlFor="salario" className="text-xs font-bold uppercase text-slate-400">Salário (R$)</label>
              <Input id="salario" type="text" placeholder="5000.00" className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600" />
            </div>

            {/* Linha 4 */}
            <div className="flex flex-col gap-2">
              <label htmlFor="endereco" className="text-xs font-bold uppercase text-slate-400">Endereço</label>
              <Input id="endereco" type="text" placeholder="Rua dos Pixels, 256" className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600" />
            </div>

            {/* Linha 5 */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col gap-2">
                <label htmlFor="cidade" className="text-xs font-bold uppercase text-slate-400">Cidade</label>
                <Input id="cidade" type="text" placeholder="São Paulo" className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600" />
              </div>
              <div className="flex flex-col gap-2">
                <label htmlFor="estado" className="text-xs font-bold uppercase text-slate-400">Estado</label>
                {/* Usando select nativo para evitar a necessidade de importar novos componentes UI do shadcn */}
                <select 
                  id="estado" 
                  className="flex h-10 w-full bg-[#242b3d] border border-slate-700 px-3 py-2 text-sm text-slate-300 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 rounded-none"
                  defaultValue=""
                >
                  <option value="" disabled>Selecione...</option>
                  <option value="SP">São Paulo</option>
                  <option value="RJ">Rio de Janeiro</option>
                  <option value="MG">Minas Gerais</option>
                  {/* Adicione os outros estados aqui */}
                </select>
              </div>
            </div>

            {/* Botões */}
            <div className="flex flex-col sm:flex-row gap-3 mt-4">
              <Button className="flex-1 bg-[#00ff66] text-black hover:bg-[#00cc52] font-bold uppercase rounded-none border-b-4 border-r-4 border-[#00993d] transition-all active:translate-y-1 active:translate-x-1 active:border-b-0 active:border-r-0 h-12">
                ▶ Enviar Solicitação
              </Button>
              <Button className="bg-[#ff3333] text-white hover:bg-[#cc0000] font-bold uppercase rounded-none border-b-4 border-r-4 border-[#990000] transition-all active:translate-y-1 active:translate-x-1 active:border-b-0 active:border-r-0 h-12 sm:w-1/3">
                Simular Erro
              </Button>
            </div>
          </CardContent>

          <CardFooter className="flex flex-col gap-4 items-center pt-6 pb-4">
            {/* Linha Tracejada */}
            <hr className="w-full border-t border-dashed border-slate-700" />
            
            <Link to="/login" className="text-sm text-slate-500 hover:text-slate-300 transition-colors">
              ← Já tem conta? Faça login
            </Link>
          </CardFooter>
        </Card>
      </CrtMonitor>
    </div>
  );
}