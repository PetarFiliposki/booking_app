import { Link, useNavigate } from "react-router-dom";
import { getToken, getUser, logout } from "../api";

export default function Navbar() {
    const navigate = useNavigate();
    const user = getUser();

    function handleLogout() {
        logout();
        navigate("/login");
    }

    return (
        <nav style={{ display: "flex", gap: 16, padding: 12, borderBottom: "1px solid #ccc" }}>
            <Link to="/"><strong>Termin</strong></Link>
            <Link to="/">Бизниси</Link>
            {getToken() ? (
                <>
                    <Link to="/my-bookings">Мои резервации</Link>
                    <span style={{ marginLeft: "auto" }}>{user?.fullName}</span>
                    <button onClick={handleLogout}>Одјава</button>
                </>
            ) : (
                <>
                    <span style={{ marginLeft: "auto" }} />
                    <Link to="/login">Најава</Link>
                    <Link to="/register">Регистрација</Link>
                </>
            )}
        </nav>
    );
}