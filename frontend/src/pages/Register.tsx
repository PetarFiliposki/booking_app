import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api";

export default function Register() {
    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    async function submit(e: React.FormEvent) {
        e.preventDefault();
        setError("");
        try {
            await api("/api/auth/register", {
                method: "POST",
                body: JSON.stringify({ fullName, email, password }),
            });
            navigate("/login");
        } catch (err) {
            setError((err as Error).message);
        }
    }

    return (
        <form onSubmit={submit} style={{ maxWidth: 320, margin: "40px auto", display: "grid", gap: 8 }}>
            <h2>Регистрација</h2>
            <input placeholder="Име и презиме" value={fullName} onChange={(e) => setFullName(e.target.value)} />
            <input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
            <input type="password" placeholder="Лозинка (најмалку 8 знаци)" value={password}
                   onChange={(e) => setPassword(e.target.value)} />
            <button type="submit">Регистрирај се</button>
            {error && <p style={{ color: "crimson" }}>{error}</p>}
        </form>
    );
}