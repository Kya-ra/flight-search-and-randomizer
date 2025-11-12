import { useState } from "react";
import { registerLocale } from "react-datepicker";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { enIE } from "date-fns/locale/en-IE";
import FlightCard from "./FlightCard";

registerLocale("en-IE", enIE);

interface Flight {
  origin: string;
  destination: string;
  price: number;
  airline: string;
  airlineLogo: string;
  airplane: string;
  arrivalTime: string;
  departureTime: string;
  durationMinutes: number;
  layovers: number;
  currency: string;
}

interface FlightSearchResponse {
  origin: string;
  destination: string;
  outboundDate: string;
  returnDate: string;
  totalResults: number;
  cheapestPrice: number;
  flights: Flight[];
}

export default function SearchForm() {
  const [form, setForm] = useState({
    origin: "",
    destination: "",
    outbound: null as Date | null,
    return: null as Date | null,
  });

  const [flightData, setFlightData] = useState<FlightSearchResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  }

    async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);
    setFlightData(null);
    setLoading(true);

    try {
      const flight_type = form.return ? 1 : 2;

      const outboundDateStr = form.outbound
        ? form.outbound.toISOString().split("T")[0]
        : "";
      const returnDateStr = form.return
        ? form.return.toISOString().split("T")[0]
        : undefined;

      const params = new URLSearchParams({
        origin: form.origin,
        destination: form.destination,
        outboundDate: outboundDateStr,
        flight_type: flight_type.toString(),
      });

      if (returnDateStr) {
        params.append("returnDate", returnDateStr);
      }

      const response = await fetch(
        `http://localhost:8080/api/flights/search?${params.toString()}`
      );

      if (!response.ok)
        throw new Error(`HTTP error! status: ${response.status}`);

      const data: FlightSearchResponse = await response.json();
      console.log("Flight search response:", data);

      setFlightData(data);
    } catch (err: any) {
      console.error("Error fetching flights:", err);
      setError(err.message || "Error fetching flights");
    } finally {
      setLoading(false);
    }
  }

    return (
    <div className="p-6">
      <form onSubmit={handleSubmit} className="flex flex-col gap-3 max-w-md">
        <input
          name="origin"
          value={form.origin}
          onChange={handleChange}
          placeholder="Origin"
          required
        />

        <input
          name="destination"
          value={form.destination}
          onChange={handleChange}
          placeholder="Destination"
          required
        />

        <DatePicker
          selected={form.outbound}
          onChange={(date) => setForm((prev) => ({ ...prev, outbound: date }))}
          placeholderText="Outbound"
          dateFormat="dd/MM/yyyy"
          locale="en-IE"
          required
        />

        <DatePicker
          selected={form.return}
          onChange={(date) => setForm((prev) => ({ ...prev, return: date }))}
          placeholderText="Return"
          dateFormat="dd/MM/yyyy"
          locale="en-IE"
        />

        <button type="submit" className="bg-blue-600 text-white py-2 rounded">
          Search
        </button>
      </form>

      {}
      {loading && <p className="mt-4 text-gray-600">Loading flights…</p>}
      {error && <p className="mt-4 text-red-500">{error}</p>}

      {}
      {flightData && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">
            {flightData.origin} → {flightData.destination}
          </h2>
          <FlightCard data={flightData} />
        </div>
      )}
    </div>
  );
}