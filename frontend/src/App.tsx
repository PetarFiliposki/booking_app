import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Businesses from "./pages/Businesses";
import Business from "./pages/Business";
import MyBookings from "./pages/MyBookings";

export default function App() {
    return (
        <BrowserRouter>
            <Navbar />
            <Routes>
                <Route path="/" element={<Businesses />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/business/:id" element={<Business />} />
                <Route path="/my-bookings" element={<MyBookings />} />
            </Routes>
        </BrowserRouter>
    );
}