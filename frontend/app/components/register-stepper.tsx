import { defineStepper } from "@stepperize/react";
import React, { useEffect, useState } from "react";
import { Link } from "react-router";
import { useCpfMask } from "~/lib/pipe/cpf-mask";
import { useCepMask } from "~/lib/pipe/cep-mask";
import { usePhoneMask } from "~/lib/pipe/phone-mask";
import { useCurrencyMask } from "~/lib/pipe/currency-mask";
import { Button } from "~/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "~/components/ui/card";
import { Input } from "~/components/ui/input";
import { getAddress } from "~/services/via-cep";
import { toast } from "sonner";
import { UfSelect } from "./uf-select";

//Constantes

const STORAGE_KEY = "retro-bank:cadastro";

type FormData = {
  cpf: string;
  nome: string;
  email: string;
  telefone: string;
  salario: string;
  cep: string;
  uf: string;
  logradouro: string;
  cidade: string;
  bairro: string;
  complemento: string;
  numero: string;
};

type StepId = "dados" | "endereco" | "revisao";

type SavedState = {
  form: FormData;
  step: StepId;
};

const EMPTY_FORM: FormData = {
  cpf: "",
  nome: "",
  email: "",
  telefone: "",
  salario: "",
  cep: "",
  uf: "",
  logradouro: "",
  cidade: "",
  bairro: "",
  complemento: "",
  numero: "",
};

//Helpers

function getSavedState(): SavedState | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    return JSON.parse(raw) as SavedState;
  } catch {
    return null;
  }
}

function saveState(form: FormData, step: StepId) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ form, step }));
}

function clearSavedState() {
  localStorage.removeItem(STORAGE_KEY);
}

//Stepper definition

const { Stepper, useStepper } = defineStepper(
  { id: "dados", title: "Dados" },
  { id: "endereco", title: "Endereço" },
  { id: "revisao", title: "Revisão" },
);

//Root: handles restore dialog + initial step

export function RegisterStepper() {
  const [savedState, setSavedState] = useState<SavedState | null>(null);
  const [initialForm, setInitialForm] = useState<FormData>(EMPTY_FORM);
  const [initialStep, setInitialStep] = useState<StepId>("dados");
  const [ready, setReady] = useState(false);
  const [checked, setChecked] = useState(false);

  // Checa localStorage só no client, após hydration
  useEffect(() => {
    const saved = getSavedState();
    if (saved) {
      setSavedState(saved);
    } else {
      setReady(true);
    }
    setChecked(true);
  }, []);

  if (!checked) return null;

  function handleRestore() {
    if (savedState) {
      setInitialForm(savedState.form);
      setInitialStep(savedState.step);
    }
    setReady(true);
  }

  function handleDiscard() {
    clearSavedState();
    setReady(true);
  }

  return (
    <>
      {!ready && savedState ? (
        <Card className="w-full max-w-md pointer-events-auto">
            <CardHeader className="items-center">
            <CardTitle className="text-center text-base text-primary retro-glow">
                Cadastro em andamento
            </CardTitle>
            </CardHeader>
            <CardContent className="flex flex-col gap-3">
            <p className="text-sm text-muted-foreground">
                Encontramos um cadastro não finalizado. Deseja continuar de onde parou?
            </p>
            <div className="flex gap-2">
                <Button variant="outline" className="flex-1" onClick={handleDiscard}>
                Começar do zero
                </Button>
                <Button className="flex-1" onClick={handleRestore}>
                Restaurar
                </Button>
            </div>
            </CardContent>
        </Card>
        ) : ready ? (
        <Stepper.Root initialStep={initialStep}>
            <RegisterStepperContent initialForm={initialForm} />
        </Stepper.Root>
    ) : null}
    </>
  );
}

//Inner content: lifecycle, validation, CEP reveal

function RegisterStepperContent({ initialForm }: { initialForm: FormData }) {
  const stepper = useStepper();
  const cpfRef = useCpfMask();
  const cepRef = useCepMask();
  const phoneRef = usePhoneMask();
  const currencyRef = useCurrencyMask();


  const [form, setForm] = useState<FormData>(initialForm);

  //CEP validity → reveal address fields
  const cepLimpo = form.cep.replace(/\D/g, "");
  const cepValido = cepLimpo.length === 8;

  //Lifecycle: onBeforeTransition (validation + goTo guard)
  useEffect(() => {
    const unsub = stepper.lifecycle.onBeforeTransition((ctx) => {
      if (ctx.direction === "goTo") {
        // Going backward always allow
        if (ctx.toIndex <= ctx.fromIndex) return;

        // Going forward only allow to the immediate next step
        if (ctx.toIndex > ctx.fromIndex + 1) {
          toast.error("Complete as etapas na ordem");
          return false;
        }
        // Immediate next step → validate current (same as "next")
      }

      if (ctx.direction === "prev") return;

      if (ctx.from.id === "dados") {
        const missing: string[] = [];
        if (!form.cpf.trim()) missing.push("CPF");
        if (!form.nome.trim()) missing.push("Nome");
        if (!form.email.trim()) missing.push("E-mail");
        if (!form.telefone.trim()) missing.push("Telefone");
        if (!form.salario.trim()) missing.push("Salário");
        if (missing.length > 0) {
          toast.error(`Preencha: ${missing.join(", ")}`);
          return false;
        }
      }

      if (ctx.from.id === "endereco") {
        const missing: string[] = [];
        if (!cepValido) missing.push("CEP (8 dígitos)");
        if (!form.uf.trim()) missing.push("UF");
        if (!form.logradouro.trim()) missing.push("Logradouro");
        if (!form.cidade.trim()) missing.push("Cidade");
        if (!form.bairro.trim()) missing.push("Bairro");
        if (!form.numero.trim()) missing.push("Número");
        if (missing.length > 0) {
          toast.error(`Preencha: ${missing.join(", ")}`);
          return false;
        }
      }
    });
    return () => unsub();
  }, [stepper, form, cepValido]);

  //Lifecycle: onAfterTransition (persist + autofocus CEP)
  useEffect(() => {
    const unsub = stepper.lifecycle.onAfterTransition((ctx) => {
      // Persist progress to localStorage
      saveState(form, ctx.to.id as StepId);

      // Focus CEP input when entering address step
      if (ctx.to.id === "endereco") {
        setTimeout(() => document.getElementById("cep")?.focus(), 100);
      }
    });
    return () => unsub();
  }, [stepper, form]);

  //Handlers

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  }

    // Auto-fetch quando CEP fica válido
    useEffect(() => {
      if (cepValido) {
        buscarCep(cepLimpo);
      }
    }, [cepValido]);

  async function buscarCep(cep: string) {
    try {
      const addr = await getAddress(cep);
      if ("erro" in addr) {
        toast.error("CEP não encontrado na base, preencha manualmente");
        return;
      }
      setForm((prev) => ({
        ...prev,
        logradouro: addr.logradouro || prev.logradouro,
        bairro: addr.bairro || prev.bairro,
        cidade: addr.localidade || prev.cidade,
        uf: addr.uf || prev.uf,
        complemento: addr.complemento || prev.complemento,
      }));
    } catch {
      toast.error("Erro ao buscar CEP");
    }
  }

  return (
    <Card className="w-full max-w-md pointer-events-auto">
      <CardHeader className="items-center pb-2">
        <CardTitle className="text-center text-base text-primary retro-glow">
          CADASTRO
        </CardTitle>

        {/* Step indicators */}
        <Stepper.List className="mt-4 flex w-full items-center gap-0">
          {stepper.state.all.map((step, i) => (
            <React.Fragment key={step.id}>
              <Stepper.Item step={step.id}>
                <Stepper.Trigger className="flex cursor-pointer flex-col items-center gap-1">
                  <Stepper.Indicator className="flex h-7 w-7 items-center justify-center border-2 text-xs font-bold data-[status=active]:border-primary data-[status=active]:bg-primary data-[status=active]:text-primary-foreground data-[status=success]:border-green-500 data-[status=success]:bg-green-500 data-[status=success]:text-white data-[status=inactive]:border-muted-foreground/40 data-[status=inactive]:text-muted-foreground/60">
                    {i + 1}
                  </Stepper.Indicator>
                  <Stepper.Title className="text-xs uppercase tracking-wider data-[status=active]:text-primary data-[status=success]:text-green-400 data-[status=inactive]:text-muted-foreground/50">
                    {step.title}
                  </Stepper.Title>
                </Stepper.Trigger>
              </Stepper.Item>
              {i < stepper.state.all.length - 1 && (
                <Stepper.Separator className="mb-4 h-0.5 flex-1 data-[status=success]:bg-green-500 data-[status=active]:bg-primary/40 data-[status=inactive]:bg-muted-foreground/20" />
              )}
            </React.Fragment>
          ))}
        </Stepper.List>
      </CardHeader>

      <CardContent className="flex flex-col gap-4">
        {/* Etapa 1: Dados pessoais */}
        <Stepper.Content step="dados">
          <div className="flex flex-col gap-3">
            <div className="flex flex-col gap-1">
              <label htmlFor="cpf" className="text-sm font-medium">
                CPF *
              </label>
              <Input
                ref={cpfRef}
                id="cpf"
                name="cpf"
                type="text"
                autoComplete="off"
                placeholder="000.000.000-00"
                value={form.cpf}
                onChange={handleChange}
              />
            </div>
            <div className="flex flex-col gap-1">
              <label htmlFor="nome" className="text-sm font-medium">
                Nome *
              </label>
              <Input
                id="nome"
                name="nome"
                type="text"
                autoComplete="name"
                placeholder="João da Silva"
                value={form.nome}
                onChange={handleChange}
              />
            </div>
            <div className="flex flex-col gap-1">
              <label htmlFor="email" className="text-sm font-medium">
                E-mail *
              </label>
              <Input
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                placeholder="joao@retrobank.com"
                value={form.email}
                onChange={handleChange}
              />
            </div>
            <div className="flex flex-col gap-1">
              <label htmlFor="telefone" className="text-sm font-medium">
                Telefone *
              </label>
              <Input
                ref={phoneRef}
                id="telefone"
                name="telefone"
                type="tel"
                autoComplete="tel"
                placeholder="(41) 99999-9999"
                value={form.telefone}
                onChange={handleChange}
              />
            </div>
            <div className="flex flex-col gap-1">
              <label htmlFor="salario" className="text-sm font-medium">
                Salário (R$) *
              </label>
              <Input
                ref={currencyRef}
                id="salario"
                name="salario"
                inputMode="numeric"
                placeholder="R$ 0,00"
                value={form.salario}
                onChange={handleChange}
              />
            </div>
          </div>
        </Stepper.Content>

        {/* Etapa 2: Endereço (CEP reveal + auto-fill) */}
        <Stepper.Content step="endereco">
          <div className="flex flex-col gap-3">
            {/* CEP sempre visível */}
            <div className="flex flex-col gap-1">
              <label htmlFor="cep" className="text-sm font-medium">
                CEP *
              </label>
              <Input
                ref={cepRef}
                id="cep"
                name="cep"
                type="text"
                autoComplete="postal-code"
                placeholder="80000-000"
                value={form.cep}
                onChange={handleChange}
              />
              {!cepValido && cepLimpo.length > 0 && (
                <p className="text-xs text-red-400">
                  Digite os 8 dígitos do CEP para continuar
                </p>
              )}
            </div>

            {/* Campos de endereço: revelados após CEP válido */}
            {cepValido && (
              <>
                <div className="flex gap-2 flex-1">
                  <div className="flex flex-1 flex-col gap-1">
                    <UfSelect
                      id="uf"
                      value={form.uf}
                      onValueChange={(value) =>
                        setForm((prev) => ({ ...prev, uf: value }))
                      }
                    />
                  </div>
                  <div className="flex flex-1 flex-col gap-1">
                    <label htmlFor="cidade" className="text-sm font-medium">
                      Cidade *
                    </label>
                    <Input
                      id="cidade"
                      name="cidade"
                      type="text"
                      placeholder="Curitiba"
                      value={form.cidade}
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div className="flex flex-col gap-1">
                  <label htmlFor="logradouro" className="text-sm font-medium">
                    Logradouro *
                  </label>
                  <Input
                    id="logradouro"
                    name="logradouro"
                    type="text"
                    autoComplete="street-address"
                    placeholder="Rua das Flores"
                    value={form.logradouro}
                    onChange={handleChange}
                  />
                </div>
                <div className="flex gap-2">
                  <div className="flex flex-1 flex-col gap-1">
                    <label htmlFor="bairro" className="text-sm font-medium">
                      Bairro *
                    </label>
                    <Input
                      id="bairro"
                      name="bairro"
                      type="text"
                      placeholder="Centro"
                      value={form.bairro}
                      onChange={handleChange}
                    />
                  </div>
                  <div className="flex w-28 flex-col gap-1">
                    <label htmlFor="numero" className="text-sm font-medium">
                      Número *
                    </label>
                    <Input
                      id="numero"
                      name="numero"
                      type="text"
                      placeholder="123"
                      value={form.numero}
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div className="flex flex-col gap-1">
                  <label htmlFor="complemento" className="text-sm font-medium">
                    Complemento
                  </label>
                  <Input
                    id="complemento"
                    name="complemento"
                    type="text"
                    placeholder="Apto 101"
                    value={form.complemento}
                    onChange={handleChange}
                  />
                </div>
              </>
            )}
          </div>
        </Stepper.Content>

        {/* Etapa 3: Revisão */}
        <Stepper.Content step="revisao">
          <div className="flex flex-col gap-3 text-sm">
            <p className="text-muted-foreground">Revise seus dados:</p>

            <div className="flex flex-col gap-2 border border-border p-3">
              <p className="font-medium text-primary retro-glow">Dados pessoais</p>
              <p>
                <span className="text-muted-foreground">CPF:</span>{" "}
                {form.cpf || "—"}
              </p>
              <p>
                <span className="text-muted-foreground">Nome:</span>{" "}
                {form.nome || "—"}
              </p>
              <p>
                <span className="text-muted-foreground">E-mail:</span>{" "}
                {form.email || "—"}
              </p>
              <p>
                <span className="text-muted-foreground">Telefone:</span>{" "}
                {form.telefone || "—"}
              </p>
              <p>
                <span className="text-muted-foreground">Salário:</span>{" "}
                {form.salario || "—"}
              </p>
            </div>

            <div className="flex flex-col gap-2 border border-border p-3">
              <p className="font-medium text-primary retro-glow">Endereço</p>
              <p>
                <span className="text-muted-foreground">CEP:</span>{" "}
                {form.cep || "—"}{" "}
                <span className="text-muted-foreground">UF:</span>{" "}
                {form.uf || "—"}
              </p>
              <p>
                <span className="text-muted-foreground">Logradouro:</span>{" "}
                {form.logradouro || "—"}, {form.numero || "s/n"}
              </p>
              <p>
                <span className="text-muted-foreground">Bairro:</span>{" "}
                {form.bairro || "—"}{" "}
                <span className="text-muted-foreground">Cidade:</span>{" "}
                {form.cidade || "—"}
              </p>
              {form.complemento && (
                <p>
                  <span className="text-muted-foreground">Complemento:</span>{" "}
                  {form.complemento}
                </p>
              )}
            </div>
          </div>
        </Stepper.Content>

        {/* Navigation */}
        <Stepper.Actions className="flex gap-2">
          <Stepper.Prev
            render={(props) => (
              <Button {...props} type="button" variant="outline" className="flex-1">
                Voltar
              </Button>
            )}
          />
          <Stepper.Next
            render={(props) => (
              <Button
                {...props}
                type="button"
                className="flex-1 hover:bg-primary/80"
                {...(stepper.state.isLast && {
                onClick: () => {
                  toast.success("Conta criada com sucesso!");
                  clearSavedState();
                },
              })}
              >
                {stepper.state.isLast ? "Criar conta" : "Próximo"}
              </Button>
            )}
          />
        </Stepper.Actions>
      </CardContent>

      <CardFooter className="flex flex-col gap-2 items-center">
        <Link
          to="/login"
          viewTransition
          className="text-sm text-muted-foreground underline hover:text-foreground"
        >
          Já tenho uma conta
        </Link>
        <Link
          to="/"
          viewTransition
          className="text-sm text-muted-foreground underline hover:text-foreground"
        >
          Voltar ao início
        </Link>
      </CardFooter>
    </Card>
  );
}