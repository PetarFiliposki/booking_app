import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api, getToken } from "../api";
import type { Availability, Booking, Business as BusinessType, ServiceItem } from "../types";

export default function Business() {
    const { id } = useParams();                       // id од адресата /business/:id
    const [business, setBusiness] = useState<BusinessType | null>(null);
    const [services, setServices] = useState<ServiceItem[]>([]);
    const [serviceId, setServiceId] = useState<number | null>(null);
    const [date, setDate] = useState("");
    const [slots, setSlots] = useState<string[]>([]);
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    // 1) Вчитај бизнис и услуги при отворање на страницата
    useEffect(() => {
        api<BusinessType>(`/api/businesses/${id}`).then(setBusiness).catch((e) => setError(e.message));
        api<ServiceItem[]>(`/api/businesses/${id}/services`).then(setServices).catch((e) => setError(e.message));
    }, [id]);

    // 2) Кога се менува услуга или датум, вчитај слободни термини
    useEffect(() => {
        setSlots([]);
        if (!serviceId || !date) return;
        api<Availability>(`/api/businesses/${id}/availability?serviceId=${serviceId}&date=${date}`)
            .then((a) => setSlots(a.slots))
            .catch((e) => setError(e.message));
    }, [id, serviceId, date]);

    // 3) Резервирај
    async function book(time: string) {
        setMessage("");
        setError("");
        try {
            await api<Booking>(`/api/businesses/${id}/bookings`, {
                method: "POST",
                body: JSON.stringify({ serviceId, date, time: time.slice(0, 5) }),
            });
            setMessage(`Успешно резервиран термин во ${time.slice(0, 5)}`);
            // освежи ги термините (зафатениот треба да исчезне)
            const a = await api<Availability>(
                `/api/businesses/${id}/availability?serviceId=${serviceId}&date=${date}`
            );
            setSlots(a.slots);
        } catch (e) {
            setError((e as Error).message);
        }
    }

    const today = new Date().toISOString().slice(0, 10);

    return (
        <div style={{ maxWidth: 600, margin: "40px auto" }}>
            <Link to="/">← Назад</Link>
            <h2>{business?.name}</h2>
            <p>{business?.description}</p>

            <h3>1. Избери услуга</h3>
            {services.length === 0 && <p>Нема услуги.</p>}
            {services.map((s) => (
                <label key={s.id} style={{ display: "block" }}>
                    <input
                        type="radio"
                        name="service"
                        checked={serviceId === s.id}
                        onChange={() => setServiceId(s.id)}
                    />
                    {" "}{s.name} ({s.durationMinutes} мин, {s.price} ден.)
                </label>
            ))}

            <h3>2. Избери датум</h3>
            <input type="date" min={today} value={date} onChange={(e) => setDate(e.target.value)} />

            <h3>3. Слободни термини</h3>
            {serviceId && date && slots.length === 0 && <p>Нема слободни термини.</p>}
            <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
                {slots.map((t) => (
                    <button key={t} onClick={() => book(t)}>{t.slice(0, 5)}</button>
                ))}
            </div>

            {!getToken() && <p>За резервација мора да сте <Link to="/login">најавени</Link>.</p>}
            {message && <p style={{ color: "green" }}>{message}</p>}
            {error && <p style={{ color: "crimson" }}>{error}</p>}
        </div>
    );
}