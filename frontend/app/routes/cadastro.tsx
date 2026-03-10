import { Link } from 'react-router';
import { CrtMonitor } from '~/components/crt-monitor';
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { Button } from '~/components/ui/button';
import { Select } from '~/components/ui/select';
import { useState } from 'react';
import { toast } from 'sonner';

export default function Autocadastro() {
  const estadosBrasileiros = [
    { value: 'SP', label: 'São Paulo' },
    { value: 'RJ', label: 'Rio de Janeiro' },
    { value: 'MG', label: 'Minas Gerais' },
    { value: 'RS', label: 'Rio Grande do Sul' },
    { value: 'BA', label: 'Bahia' },
  ];

  const [estadoSelecionado, setEstadoSelecionado] = useState<string>('');

  return (
    <div className="crt-page">
      <CrtMonitor title="">
        <Card className="w-full max-w-2xl pointer-events-auto bg-[#1a1f2e] border-slate-700 text-slate-300 rounded-none">
          <CardHeader className="items-center pb-6">
            <CardTitle className="text-center text-lg text-[#f8f8f2] tracking-wider uppercase">
              Autocadastro
            </CardTitle>
          </CardHeader>

          <CardContent className="flex flex-col gap-5">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col gap-2">
                <label htmlFor="nome" className="text-xs font-bold uppercase text-slate-400">
                  Nome Completo
                </label>
                <Input
                  id="nome"
                  type="text"
                  placeholder="João da Silva"
                  className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600"
                />
              </div>
              <div className="flex flex-col gap-2">
                <label htmlFor="cpf" className="text-xs font-bold uppercase text-slate-400">
                  CPF
                </label>
                <Input
                  id="cpf"
                  type="text"
                  placeholder="000.000.000-00"
                  className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col gap-2">
                <label htmlFor="email" className="text-xs font-bold uppercase text-slate-400">
                  E-mail
                </label>
                <Input
                  id="email"
                  type="email"
                  placeholder="joao@email.com"
                  className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600"
                />
              </div>
              <div className="flex flex-col gap-2">
                <label htmlFor="telefone" className="text-xs font-bold uppercase text-slate-400">
                  Telefone
                </label>
                <Input
                  id="telefone"
                  type="text"
                  placeholder="(00) 00000-0000"
                  className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600"
                />
              </div>
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="salario" className="text-xs font-bold uppercase text-slate-400">
                Salário (R$)
              </label>
              <Input
                id="salario"
                type="text"
                placeholder="5000.00"
                className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600"
              />
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="endereco" className="text-xs font-bold uppercase text-slate-400">
                Endereço
              </label>
              <Input
                id="endereco"
                type="text"
                placeholder="Rua dos Pixels, 256"
                className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600"
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col gap-2">
                <label htmlFor="cidade" className="text-xs font-bold uppercase text-slate-400">
                  Cidade
                </label>
                <Input
                  id="cidade"
                  type="text"
                  placeholder="São Paulo"
                  className="bg-[#242b3d] border-slate-700 rounded-none placeholder:text-slate-600"
                />
              </div>
              <div className="flex flex-col gap-2">
                <Select
                  id="estado-single"
                  label="Estado"
                  placeholder="Selecione um estado..."
                  options={estadosBrasileiros}
                  value={estadoSelecionado}
                  onValueChange={(valor) => {
                    setEstadoSelecionado(valor);
                    console.log('Selecionado:', valor);
                  }}
                />
              </div>
            </div>

            <div className="flex flex-col sm:flex-row gap-3 mt-4">
              <Button
                onClick={() => toast.success('Solicitação enviada com sucesso!')}
                className="flex-1 bg-[#00ff66] text-black hover:bg-[#00cc52] font-bold uppercase rounded-none border-b-4 border-r-4 border-[#00993d] transition-all active:translate-y-1 active:translate-x-1 active:border-b-0 active:border-r-0 h-12"
              >
                ▶ Enviar Solicitação
              </Button>
              <Button className="bg-[#ff3333] text-white hover:bg-[#cc0000] font-bold uppercase rounded-none border-b-4 border-r-4 border-[#990000] transition-all active:translate-y-1 active:translate-x-1 active:border-b-0 active:border-r-0 h-12 sm:w-1/3">
                Simular Erro
              </Button>
            </div>
          </CardContent>

          <CardFooter className="flex flex-col gap-4 items-center pt-6 pb-4">
            <hr className="w-full border-t border-dashed border-slate-700" />

            <Link
              to="/login"
              className="text-sm text-slate-500 hover:text-slate-300 transition-colors"
            >
              ← Já tem conta? Faça login
            </Link>
          </CardFooter>
        </Card>
      </CrtMonitor>
    </div>
  );
}
