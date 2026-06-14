import { env } from "cloudflare:workers";
import { redirect } from "react-router";
import { destroySession, getSession } from "~/auth/sessions.server";

const API_URL = env.API_URL;

function myFetch(request: Request) {
    return async (
        endpoint: string,
        init?: RequestInit
    ): Promise<Response> => {
        const session = await getSession(request.headers.get("Cookie"));
        const token = session.get("token");

        const response = await fetch(`${API_URL}${endpoint}`, {
            ...init,
            headers: {
                ...(init?.body !== undefined ? { "Content-Type": "application/json" } : {}),
                ...(token ? { Authorization: `Bearer ${token}`} : {}),
                ...init?.headers,
            }
        });

        if (response.status === 401 && token) {
            throw redirect("/", {
                headers: {
                    "Set-Cookie": await destroySession(session),
                },
            });
        }

        return response;
    }
}

export function api(request: Request) {
    const fetcher = myFetch(request);

    return {
        get: (endpoint: string, init?: RequestInit) =>
            fetcher(endpoint, { ...init, method: "GET" }),

        post: (endpoint: string, body?: unknown, init?: RequestInit) => 
            fetcher(endpoint, {
                ...init,
                method: "POST",
                ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
            }),

        put: (endpoint: string, body?: unknown, init?: RequestInit) =>
            fetcher(endpoint, {
                ...init,
                method: "PUT",
                body: JSON.stringify(body),
            }),

        patch: (endpoint: string, body?: unknown, init?: RequestInit) =>
            fetcher(endpoint, {
                ...init,
                method: "PATCH",
                body: JSON.stringify(body),
            }),

        delete: (endpoint: string, init?: RequestInit) =>
            fetcher(endpoint, { ...init, method: "DELETE" }),
    };
}

