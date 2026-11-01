"use client";

import { useEffect, useState } from "react";
import Link from "next/link";

interface Reservation {
  id: number;
  roomType: { id: number; name: string };
  room: { id: number; roomNumber: string } | null;
  guest: { firstName: string; lastName: string; email: string; phoneNumber?: string };
  hotelPackage: { id: number; name: string };
  checkInDate: string;
  checkOutDate: string;
  totalPrice: number;
  status: string;
}

interface Room {
  id: number;
  roomNumber: string;
  status: string;
  roomType: { id: number; name: string };
}

export default function DashboardPage() {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [rooms, setRooms] = useState<Room[]>([]);
  const [selectedRooms, setSelectedRooms] = useState<{ [reservationId: number]: number }>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const API_BASE = "https://hotel-backend-hita.onrender.com/api";

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [resRes, resRooms] = await Promise.all([
        fetch(`${API_BASE}/reservations`),
        fetch(`${API_BASE}/rooms`),
      ]);

      if (resRes.ok) {
        const resData = await resRes.json();
        setReservations(resData);
      }
      if (resRooms.ok) {
        const roomsData = await resRooms.json();
        setRooms(roomsData);
      }
    } catch (err: any) {
      setError(err.message || "Greška pri dohvatu podataka.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCheckIn = async (reservationId: number) => {
    const roomId = selectedRooms[reservationId];
    if (!roomId) {
      alert("Molimo odaberite broj sobe za dodjelu pri prijavi.");
      return;
    }

    try {
      const res = await fetch(`${API_BASE}/reservations/${reservationId}/check-in?roomId=${roomId}`, {
        method: "PUT",
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Greška pri prijavi.");
      }

      await loadData();
    } catch (err: any) {
      alert(err.message);
    }
  };

  return (
    <main className="min-h-screen bg-slate-50 p-6 text-slate-800">
      <div className="max-w-6xl mx-auto">
        <header className="flex justify-between items-center mb-8 border-b pb-4">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">Recepcijski Dashboard</h1>
            <p className="text-sm text-slate-500">Upravljanje dolascima, odlascima i dodjela soba</p>
          </div>
          <Link
            href="/"
            className="border border-slate-300 bg-white px-4 py-2 rounded-lg font-medium hover:bg-slate-100 transition"
          >
            ← Rezervacijski dio
          </Link>
        </header>

        {loading ? (
          <p className="text-slate-500">Učitavanje podataka s poslužitelja...</p>
        ) : error ? (
          <div className="p-4 bg-red-50 text-red-700 rounded-lg">{error}</div>
        ) : (
          <div className="bg-white rounded-xl border shadow-sm overflow-hidden">
            <div className="p-4 border-b bg-slate-50 font-semibold text-slate-700">
              Sve rezervacije ({reservations.length})
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse text-sm">
                <thead>
                  <tr className="border-b bg-slate-100 text-slate-600">
                    <th className="p-3">Gost</th>
                    <th className="p-3">Tip sobe</th>
                    <th className="p-3">Paket</th>
                    <th className="p-3">Termin</th>
                    <th className="p-3">Cijena</th>
                    <th className="p-3">Status</th>
                    <th className="p-3">Broj sobe</th>
                    <th className="p-3">Akcija</th>
                  </tr>
                </thead>
                <tbody className="divide-y">
                  {reservations.length === 0 ? (
                    <tr>
                      <td colSpan={8} className="p-6 text-center text-slate-500">
                        Nema unesenih rezervacija.
                      </td>
                    </tr>
                  ) : (
                    reservations.map((res) => {
                      // Filtriramo dostupne fizičke sobe istog tipa
                      const eligibleRooms = rooms.filter(
                        (r) => r.roomType?.id === res.roomType?.id && r.status === "AVAILABLE"
                      );

                      return (
                        <tr key={res.id} className="hover:bg-slate-50">
                          <td className="p-3 font-medium text-slate-900">
                            {res.guest.firstName} {res.guest.lastName}
                            <div className="text-xs text-slate-500">{res.guest.email}</div>
                          </td>
                          <td className="p-3">{res.roomType?.name}</td>
                          <td className="p-3">
                            <span className="bg-slate-100 px-2 py-1 rounded text-xs font-medium">
                              {res.hotelPackage?.name}
                            </span>
                          </td>
                          <td className="p-3 text-xs text-slate-600">
                            {res.checkInDate} do {res.checkOutDate}
                          </td>
                          <td className="p-3 font-semibold text-slate-900">{res.totalPrice} €</td>
                          <td className="p-3">
                            <span
                              className={`px-2 py-1 rounded-full text-xs font-semibold ${
                                res.status === "CONFIRMED"
                                  ? "bg-amber-100 text-amber-800"
                                  : res.status === "CHECKED_IN"
                                  ? "bg-green-100 text-green-800"
                                  : "bg-slate-100 text-slate-700"
                              }`}
                            >
                              {res.status}
                            </span>
                          </td>
                          <td className="p-3">
                            {res.room ? (
                              <span className="font-bold text-slate-800">Soba #{res.room.roomNumber}</span>
                            ) : (
                              <select
                                className="border rounded p-1 text-xs outline-none"
                                value={selectedRooms[res.id] || ""}
                                onChange={(e) =>
                                  setSelectedRooms({ ...selectedRooms, [res.id]: Number(e.target.value) })
                                }
                              >
                                <option value="">Odaberi sobu...</option>
                                {eligibleRooms.map((rm) => (
                                  <option key={rm.id} value={rm.id}>
                                    Soba {rm.roomNumber}
                                  </option>
                                ))}
                              </select>
                            )}
                          </td>
                          <td className="p-3">
                            {res.status === "CONFIRMED" && (
                              <button
                                onClick={() => handleCheckIn(res.id)}
                                className="bg-blue-600 hover:bg-blue-700 text-white text-xs px-3 py-1.5 rounded transition"
                              >
                                Check-in
                              </button>
                            )}
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </main>
  );
}