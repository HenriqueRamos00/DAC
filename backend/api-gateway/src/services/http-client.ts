import { BadGatewatError, UpstreamError } from '../hooks/errors.ts';

async function request<T>(
  method: string,
  url: string,
  body?: unknown,
  headers: Record<string, string> = {},
): Promise<T> {
  let res: Response;
  try {
    res = await fetch(url, {
      method,
      headers: { 'content-type': 'application/json', ...headers },
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch (err) {
    // Falha de rede / DNS / connection refused
    throw new BadGatewatError(new URL(url).host);
  }

  const isJson = res.headers.get('content-type')?.includes('json');
  const payload = isJson ? await res.json() : await res.text();

  if (!res.ok) throw new UpstreamError(res.status, payload, url);

  return payload as T;
}

export const httpClient = {
  get:  <T>(url: string, h?: Record<string, string>) => request<T>('GET',    url, undefined, h),
  post: <T>(url: string, b: unknown, h?: Record<string, string>) => request<T>('POST',   url, b, h),
  put:  <T>(url: string, b: unknown, h?: Record<string, string>) => request<T>('PUT',    url, b, h),
  del:  <T>(url: string, h?: Record<string, string>) => request<T>('DELETE', url, undefined, h),
};