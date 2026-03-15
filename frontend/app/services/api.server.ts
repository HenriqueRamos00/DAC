import { env } from "cloudflare:workers";
import { authCookie } from "~/auth/cookie"

const API_URL = env.API_URL;

function myFetch(request: Request) {
    return async (
        endpoint: string,
        init?: RequestInit
    ): Promise<Response> => {
        const token = await authCookie.parse(request.headers.get("Cookie"))

        return fetch(`${API_URL}${endpoint}`, {
            ...init,
            headers: {
                "Content-Type": "application/json",
                ...(token ? { Authorization: `Bearer ${token}`} : {}),
                ...init?.headers,
            }
        })
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
                body: JSON.stringify(body),
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



