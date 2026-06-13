import { destroySession, getSession } from "~/auth/sessions.server";
import type { Route } from "../+types/root";
import { redirect } from "react-router";
import { api } from "~/services/api.server";

export async function action({ request }: Route.ActionArgs) {
  const session = await getSession(request.headers.get("Cookie"));

  try {
    await api(request).post("/logout");
  } catch (error) {
    if (error instanceof Response) {
      throw error;
    }
  }

  return redirect("/", {
    headers: {
      "Set-Cookie": await destroySession(session),
    },
  });
}

export default function LogoutRoute() {
  return null;
}
