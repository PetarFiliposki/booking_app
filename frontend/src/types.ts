export interface User {
    id: number;
    email: string;
    fullName: string;
    role: string;
}

export interface AuthResponse {
    token: string;
    user: User;
}

export interface Business {
    id: number;
    name: string;
    description: string | null;
    address: string | null;
    phone: string | null;
    ownerId: number;
}

export interface ServiceItem {
    id: number;
    businessId: number;
    name: string;
    durationMinutes: number;
    price: number;
}

export interface Availability {
    businessId: number;
    serviceId: number;
    date: string;
    durationMinutes: number;
    slots: string[];
}

export interface Booking {
    id: number;
    businessId: number;
    serviceId: number;
    serviceName: string;
    startTime: string;
    endTime: string;
    status: string;
}
export interface WorkingHours {
    id: number;
    dayOfWeek: number;
    openTime: string;
    closeTime: string;
}