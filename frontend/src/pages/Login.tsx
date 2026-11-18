import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api";
import type { AuthResponse } from "../types";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    async function submit(e: React.FormEvent) {
        e.preventDefault();
        setError("");
        try {
            const data = await api<AuthResponse>("/api/auth/login", {
                method: "POST",
                body: JSON.stringify({ email, password }),
            });
            localStorage.setItem("token", data.token);
            localStorage.setItem("user", JSON.stringify(data.user));
            navigate("/");
        } catch (err) {
            setError((err as Error).message);
        }
    }

    return (
        <form onSubmit={submit} style={{ maxWidth: 320, margin: "40px auto", display: "grid", gap: 8 }}>
            <h2>Најава</h2>
            <input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
            <input
                type="password"
                placeholder="Лозинка"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button type="submit">Најави се</button>
            {error && <p style={{ color: "crimson" }}>{error}</p>}
        </form>
    );
}