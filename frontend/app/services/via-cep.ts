export type Address = {
  cep: string,
  logradouro: string,
  complemento?: string,
  unidade?: string,
  bairro: string,
  localidade: string,//é o nome da cidade
  uf: string,
  regiao: string,
  ibge: number,
  ddd: number
}

export async function getAddress(cep: string) {
  const response = await fetch(
    `https://viacep.com.br/ws/${cep}/json/`
  );

  const address = (await response.json()) as Address;

  return address;
}