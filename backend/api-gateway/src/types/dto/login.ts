import type { Roles } from "../enum/roles.ts";

export type LoginMsResponseDto = {
  nome: string;
  cpf: string;
  email: string;
  tipoUsuario: Roles;
};

export type LoginRequestDto = {
  login: string;
  senha: string;
};

export type LoginResponseDto = {
  access_token: string;
  token_type: "bearer";
  tipo: Roles;
  usuario: {
    nome: string;
    cpf: string;
    email: string;
  };
};
