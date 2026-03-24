import { getSession } from "~/auth/sessions.server";
import { api } from "~/services/api.server";

export async function getSessionAutenticada(request: Request) {
  const session = await getSession(request.headers.get("Cookie"));
  const token = session.get("token");
  const cpf = session.get("cpf");
  let conta = null;
  if (session.get("tipo") === "CLIENTE") {
    conta = session.get("conta") as string;
  }

  if (typeof token !== "string") {
    throw new Response("Unauthorized", { status: 401 });
  }

  return {
    apiClient: api(request),
    cpf: cpf as string,
    conta: conta,
    session,
  };
}
