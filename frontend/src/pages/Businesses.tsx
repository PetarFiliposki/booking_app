import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api";
import type { Business } from "../types";

export default function Businesses() {
    const [items, setItems] = useState<Business[]>([]);
    const [error, setError] = useState("");

    useEffect(() => {
        api<Business[]>("/api/businesses")
            .then(setItems)
            .catch((e) => setError(e.message));
    }, []);

    return (
        <div style={{ maxWidth: 600, margin: "40px auto" }}>
            <h2>Бизниси</h2>
            {error && <p style={{ color: "crimson" }}>{error}</p>}
            <ul>
                {items.map((b) => (
                    <li key={b.id}>
                        <Link to={`/business/${b.id}`}>
                            <strong>{b.name}</strong>
                        </Link>
                        {b.address && ` (${b.address})`}
                    </li>
                ))}
            </ul>
            <Link to="/login">Најава</Link>
        </div>
    );
}