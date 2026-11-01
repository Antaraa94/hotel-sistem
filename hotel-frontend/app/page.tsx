"use client";

import { useState } from "react";
import Link from "next/link";

interface RoomType {
  id: number;
  name: string;
  capacity: number;
  basePrice: number;
}

interface HotelPackage {
  id: number;
  name: string;
  extraPricePerNight: number;
}

interface AvailabilityData {
  availableRoomTypes: RoomType[];
  availablePackages: HotelPackage[];
}

export default function HomePage() {
  const [checkInDate, setCheckInDate] = useState("");
  const [checkOutDate, setCheckOutDate] = useState("");
  const [availability, setAvailability] = useState<AvailabilityData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Odabir sobe i paketa za rezervaciju
  const [selectedRoomType, setSelectedRoomType] = useState<RoomType | null>(null);
  const [selectedPackage, setSelectedPackage] = useState<HotelPackage | null>(null);

  // Podaci gosta
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [bookingSuccess, setBookingSuccess] = useState(false);

  const API_BASE = "https://hotel-backend-hita.onrender.com/api";

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!checkInDate || !checkOutDate) {
      setError("Molimo unesite oba datuma.");
      return;
    }
    if (checkInDate >= checkOutDate) {
      setError("Datum odlaska mora biti nakon datuma dolaska.");
      return;
    }

    setLoading(true);
    setError(null);
    setSelectedRoomType(null);
    setSelectedPackage(null);
    setBookingSuccess(false);

    try {
      const res = await fetch(
        `${API_BASE}/reservations/availability?checkIn=${checkInDate}&checkOut=${checkOutDate}`
      );
      if (!res.ok) throw new Error("Greška pri dohvatu dostupnosti.");
      const data: AvailabilityData = await res.json();
      setAvailability(data);
      if (data.availablePackages.length > 0) {
        setSelectedPackage(data.availablePackages[0]);
      }
    } catch (err: any) {
      setError(err.message || "Nešto je pošlo po zlu.");
    } finally {
      setLoading(false);
    }
  };

  const calculateTotal = () => {
    if (!selectedRoomType || !selectedPackage || !checkInDate || !checkOutDate) return 0;
    const start = new Date(checkInDate);
    const end = new Date(checkOutDate);
    const nights = Math.max(1, Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)));
    const pricePerNight = Number(selectedRoomType.basePrice) + Number(selectedPackage.extraPricePerNight);
    return pricePerNight * nights;
  };

  const handleBooking = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedRoomType || !selectedPackage) return;

    setLoading(true);
    setError(null);

    const payload = {
      roomTypeId: selectedRoomType.id,
      packageId: selectedPackage.id,
      firstName,
      lastName,
      guestEmail: email,
      phoneNumber,
      checkInDate,
      checkOutDate,
    };

    try {
      const res = await fetch(`${API_BASE}/reservations`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const errData = await res.text();
        throw new Error(errData || "Greška pri kreiranju rezervacije.");
      }

      setBookingSuccess(true);
      setSelectedRoomType(null);
      setSelectedPackage(null);
      setFirstName("");
      setLastName("");
      setEmail("");
      setPhoneNumber("");
    } catch (err: any) {
      setError(err.message || "Neuspješna rezervacija.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-slate-50 p-6 text-slate-800">
      <div className="max-w-5xl mx-auto">
        <header className="flex justify-between items-center mb-8 border-b pb-4">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">Hotel Management & Booking</h1>
            <p className="text-sm text-slate-500">Sistem rezervacije po uzoru na PMS Opera</p>
          </div>
          <Link
            href="/dashboard"
            className="bg-slate-900 text-white px-4 py-2 rounded-lg font-medium hover:bg-slate-800 transition"
          >
            Recepcijski Dashboard
          </Link>
        </header>

        {/* 1. Forma za pretragu po datumima */}
        <section className="bg-white p-6 rounded-xl shadow-sm border mb-8">
          <h2 className="text-xl font-semibold mb-4 text-slate-900">Pretraga slobodnog smještaja</h2>
          <form onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Datum dolaska</label>
              <input
                type="date"
                value={checkInDate}
                onChange={(e) => setCheckInDate(e.target.value)}
                className="w-full border rounded-lg p-2.5 outline-none focus:ring-2 focus:ring-slate-900"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Datum odlaska</label>
              <input
                type="date"
                value={checkOutDate}
                onChange={(e) => setCheckOutDate(e.target.value)}
                className="w-full border rounded-lg p-2.5 outline-none focus:ring-2 focus:ring-slate-900"
                required
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2.5 px-4 rounded-lg transition disabled:opacity-50"
            >
              {loading ? "Pretraživanje..." : "Pretraži slobodne sobe"}
            </button>
          </form>
          {error && <p className="mt-3 text-sm text-red-600">{error}</p>}
          {bookingSuccess && (
            <div className="mt-4 p-4 bg-green-50 text-green-700 rounded-lg border border-green-200">
              Uspješno ste kreirali rezervaciju! Fizički broj sobe biće dodijeljen na recepciji pri dolasku.
            </div>
          )}
        </section>

        {/* 2. Prikaz dostupnih tipova soba i paketa */}
        {availability && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-4">
              <h2 className="text-xl font-semibold text-slate-900">
                Dostupne vrste soba ({availability.availableRoomTypes.length})
              </h2>

              {availability.availableRoomTypes.length === 0 ? (
                <div className="bg-white p-6 rounded-xl border text-slate-500">
                  Nema slobodnih soba za odabrane datume. Pokušajte drugi termin.
                </div>
              ) : (
                availability.availableRoomTypes.map((rt) => (
                  <div
                    key={rt.id}
                    className={`bg-white p-5 rounded-xl border transition cursor-pointer ${
                      selectedRoomType?.id === rt.id
                        ? "border-blue-600 ring-2 ring-blue-600 shadow-sm"
                        : "hover:border-slate-300"
                    }`}
                    onClick={() => setSelectedRoomType(rt)}
                  >
                    <div className="flex justify-between items-start">
                      <div>
                        <h3 className="text-lg font-bold text-slate-900">{rt.name}</h3>
                        <p className="text-sm text-slate-500">Maksimalan kapacitet: {rt.capacity} osobe</p>
                      </div>
                      <div className="text-right">
                        <span className="text-xl font-bold text-slate-900">{rt.basePrice} €</span>
                        <span className="text-xs text-slate-500 block">/ noć (osnovna)</span>
                      </div>
                    </div>
                  </div>
                ))
              )}

              {/* Odabir paketa */}
              {selectedRoomType && (
                <div className="mt-6 bg-white p-5 rounded-xl border">
                  <h3 className="font-semibold text-slate-900 mb-3">Odaberite paket / uslugu:</h3>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    {availability.availablePackages.map((pkg) => (
                      <div
                        key={pkg.id}
                        onClick={() => setSelectedPackage(pkg)}
                        className={`p-3 border rounded-lg cursor-pointer transition ${
                          selectedPackage?.id === pkg.id
                            ? "border-blue-600 bg-blue-50"
                            : "hover:bg-slate-50"
                        }`}
                      >
                        <div className="font-medium text-slate-800">{pkg.name}</div>
                        <div className="text-sm text-slate-500">
                          {pkg.extraPricePerNight > 0
                            ? `+${pkg.extraPricePerNight} € / noć`
                            : "Uključeno u osnovnu cijenu"}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>

            {/* 3. Forma za potvrdu rezervacije */}
            <div>
              <div className="bg-white p-6 rounded-xl border shadow-sm sticky top-6">
                <h2 className="text-lg font-bold text-slate-900 mb-4">Podaci za rezervaciju</h2>

                {selectedRoomType && selectedPackage ? (
                  <form onSubmit={handleBooking} className="space-y-3">
                    <div className="p-3 bg-slate-50 rounded-lg text-sm space-y-1 mb-4">
                      <div><span className="font-medium">Soba:</span> {selectedRoomType.name}</div>
                      <div><span className="font-medium">Paket:</span> {selectedPackage.name}</div>
                      <div className="pt-2 border-t mt-2 flex justify-between font-bold text-base text-slate-900">
                        <span>Ukupna cijena:</span>
                        <span>{calculateTotal()} €</span>
                      </div>
                    </div>

                    <div>
                      <label className="block text-xs font-medium text-slate-600 mb-1">Ime</label>
                      <input
                        type="text"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        required
                        className="w-full border rounded-lg p-2 text-sm outline-none focus:ring-1 focus:ring-slate-900"
                      />
                    </div>
                    <div>
                      <label className="block text-xs font-medium text-slate-600 mb-1">Prezime</label>
                      <input
                        type="text"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        required
                        className="w-full border rounded-lg p-2 text-sm outline-none focus:ring-1 focus:ring-slate-900"
                      />
                    </div>
                    <div>
                      <label className="block text-xs font-medium text-slate-600 mb-1">Email</label>
                      <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        className="w-full border rounded-lg p-2 text-sm outline-none focus:ring-1 focus:ring-slate-900"
                      />
                    </div>
                    <div>
                      <label className="block text-xs font-medium text-slate-600 mb-1">Broj telefona</label>
                      <input
                        type="tel"
                        value={phoneNumber}
                        onChange={(e) => setPhoneNumber(e.target.value)}
                        className="w-full border rounded-lg p-2 text-sm outline-none focus:ring-1 focus:ring-slate-900"
                      />
                    </div>

                    <button
                      type="submit"
                      disabled={loading}
                      className="w-full mt-4 bg-slate-900 hover:bg-slate-800 text-white font-medium py-2.5 rounded-lg transition text-sm disabled:opacity-50"
                    >
                      {loading ? "Spremanje..." : "Potvrdi rezervaciju"}
                    </button>
                  </form>
                ) : (
                  <p className="text-sm text-slate-500">
                    Pretražite termine i kliknite na željenu sobu kako biste unijeli podatke.
                  </p>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </main>
  );
}