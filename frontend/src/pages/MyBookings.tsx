import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api, getToken } from "../api";
import type { Booking } from "../types";

export default function MyBookings() {
    const [items, setItems] = useState<Booking[]>([]);
    const [error, setError] = useState("");

    function load() {
        api<Booking[]>("/api/bookings/my").then(setItems).catch((e) => setError(e.message));
    }

    useEffect(() => {
        if (getToken()) load();
    }, []);

    async function cancel(id: number) {
        setError("");
        try {
            await api(`/api/bookings/${id}/cancel`, { method: "POST" });
            load(); // освежи ја листата
        } catch (e) {
            setError((e as Error).message);
        }
    }

    function fmt(iso: string) {
        return new Date(iso).toLocaleString("mk-MK", { timeZone: "Europe/Skopje" });
    }

    if (!getToken()) {
        return <p style={{ margin: 40 }}>Мора да сте <Link to="/login">најавени</Link>.</p>;
    }

    return (
        <div style={{ maxWidth: 600, margin: "40px auto" }}>
            <h2>Мои резервации</h2>
            {error && <p style={{ color: "crimson" }}>{error}</p>}
            {items.length === 0 && <p>Немате резервации.</p>}
            <ul>
                {items.map((b) => (
                    <li key={b.id} style={{ marginBottom: 8 }}>
                        {b.serviceName}, {fmt(b.startTime)}{" "}
                        <em>({b.status === "CONFIRMED" ? "потврдена" : "откажана"})</em>{" "}
                        {b.status === "CONFIRMED" && <button onClick={() => cancel(b.id)}>Откажи</button>}
                    </li>
                ))}
            </ul>
        </div>
    );
}