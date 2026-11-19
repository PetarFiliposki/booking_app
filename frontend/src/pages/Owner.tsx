import { useEffect, useState } from "react";
import { api, getUser } from "../api";
import type { Booking, Business, ServiceItem, WorkingHours } from "../types";

const DAYS = ["Понеделник", "Вторник", "Среда", "Четврток", "Петок", "Сабота", "Недела"];

export default function Owner() {
    const user = getUser();
    const [mine, setMine] = useState<Business[]>([]);
    const [selected, setSelected] = useState<number | null>(null);
    const [services, setServices] = useState<ServiceItem[]>([]);
    const [hours, setHours] = useState<WorkingHours[]>([]);
    const [bookings, setBookings] = useState<Booking[]>([]);
    const [error, setError] = useState("");

    const [bName, setBName] = useState("");
    const [bAddress, setBAddress] = useState("");

    const [sName, setSName] = useState("");
    const [sDuration, setSDuration] = useState(30);
    const [sPrice, setSPrice] = useState(0);

    const [day, setDay] = useState(1);
    const [open, setOpen] = useState("09:00");
    const [close, setClose] = useState("17:00");

    function loadMine() {
        api<Business[]>("/api/businesses/mine").then(setMine).catch((e) => setError(e.message));
    }

    function loadDetails(id: number) {
        api<ServiceItem[]>(`/api/businesses/${id}/services`).then(setServices);
        api<WorkingHours[]>(`/api/businesses/${id}/working-hours`).then(setHours);
        api<Booking[]>(`/api/businesses/${id}/bookings`).then(setBookings);
    }

    useEffect(loadMine, []);
    useEffect(() => {
        if (selected) loadDetails(selected);
    }, [selected]);

    async function run(fn: () => Promise<unknown>) {
        setError("");
        try {
            await fn();
        } catch (e) {
            setError((e as Error).message);
        }
    }

    const createBusiness = () =>
        run(async () => {
            await api("/api/businesses", {
                method: "POST",
                body: JSON.stringify({ name: bName, address: bAddress }),
            });
            setBName("");
            setBAddress("");
            loadMine();
        });

    const addService = () =>
        run(async () => {
            await api(`/api/businesses/${selected}/services`, {
                method: "POST",
                body: JSON.stringify({ name: sName, durationMinutes: sDuration, price: sPrice }),
            });
            setSName("");
            loadDetails(selected!);
        });

    const saveHours = () =>
        run(async () => {
            await api(`/api/businesses/${selected}/working-hours`, {
                method: "PUT",
                body: JSON.stringify({ dayOfWeek: day, openTime: open, closeTime: close }),
            });
            loadDetails(selected!);
        });

    if (user?.role !== "BUSINESS_OWNER") {
        return <p style={{ margin: 40 }}>Оваа страница е само за сопственици на бизниси.</p>;
    }

    return (
        <div style={{ maxWidth: 700, margin: "40px auto" }}>
            <h2>Табла за сопственик</h2>
            {error && <p style={{ color: "crimson" }}>{error}</p>}

            <h3>Мои бизниси</h3>
            <ul>
                {mine.map((b) => (
                    <li key={b.id}>
                        <button onClick={() => setSelected(b.id)} disabled={selected === b.id}>
                            {b.name}
                        </button>
                    </li>
                ))}
            </ul>
            <input placeholder="Име на нов бизнис" value={bName} onChange={(e) => setBName(e.target.value)} />
            <input placeholder="Адреса" value={bAddress} onChange={(e) => setBAddress(e.target.value)} />
            <button onClick={createBusiness}>Додај бизнис</button>

            {selected && (
                <>
                    <h3>Услуги</h3>
                    <ul>
                        {services.map((s) => (
                            <li key={s.id}>{s.name}, {s.durationMinutes} мин, {s.price} ден.</li>
                        ))}
                    </ul>
                    <input placeholder="Име" value={sName} onChange={(e) => setSName(e.target.value)} />
                    <input type="number" value={sDuration} onChange={(e) => setSDuration(+e.target.value)} />
                    <input type="number" value={sPrice} onChange={(e) => setSPrice(+e.target.value)} />
                    <button onClick={addService}>Додај услуга</button>

                    <h3>Работно време</h3>
                    <ul>
                        {hours.map((h) => (
                            <li key={h.id}>{DAYS[h.dayOfWeek - 1]}: {h.openTime.slice(0, 5)} до {h.closeTime.slice(0, 5)}</li>
                        ))}
                    </ul>
                    <select value={day} onChange={(e) => setDay(+e.target.value)}>
                        {DAYS.map((d, i) => <option key={i} value={i + 1}>{d}</option>)}
                    </select>
                    <input type="time" value={open} onChange={(e) => setOpen(e.target.value)} />
                    <input type="time" value={close} onChange={(e) => setClose(e.target.value)} />
                    <button onClick={saveHours}>Зачувај</button>

                    <h3>Резервации</h3>
                    {bookings.length === 0 && <p>Нема резервации.</p>}
                    <ul>
                        {bookings.map((b) => (
                            <li key={b.id}>
                                {b.serviceName}, {new Date(b.startTime).toLocaleString("mk-MK", { timeZone: "Europe/Skopje" })}
                                {" "}({b.status === "CONFIRMED" ? "потврдена" : "откажана"})
                            </li>
                        ))}
                    </ul>
                </>
            )}
        </div>
    );
}