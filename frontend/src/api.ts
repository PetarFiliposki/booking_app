const BASE = "http://localhost:8080";

export function getToken() {
    return localStorage.getItem("token");
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
    const headers: Record<string, string> = { "Content-Type": "application/json" };

    const token = getToken();
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const res = await fetch(BASE + path, { ...options, headers });
    const body = await res.json().catch(() => null);

    if (!res.ok) {
        throw new Error(body?.error ?? `Грешка ${res.status}`);
    }
    return body as T;


}

export function getUser(): { id: number; email: string; fullName: string; role: string } | null {
    const raw = localStorage.getItem("user");
    return raw ? JSON.parse(raw) : null;
}

export function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
}