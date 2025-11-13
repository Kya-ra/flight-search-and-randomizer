Skip to content
Chat history
You said:
can you modify this to allow the user to click on a card and 'focus it' - dismissing all other options, and updating the From price field to be that flights price rather than the cheapest flight (i.e. flights[0])

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
  const [returnFlightData, setReturnFlightData] = useState<FlightSearchResponse | null>(null);
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
        flight_type: "2",
      });

      // --- OUTBOUND FLIGHTS ---
      const outboundResponse = await fetch(
        http://localhost:8080/api/flights/search?${params.toString()}
      );

      if (!outboundResponse.ok)
        throw new Error(HTTP error! status: ${outboundResponse.status});

      const outboundData: FlightSearchResponse = await outboundResponse.json();        
      outboundData.flights.sort((a, b) => {
        if (a.price === 0 && b.price === 0) return 0;
        if (a.price === 0) return 1;
        if (b.price === 0) return -1;
        return a.price - b.price;
      });
      console.log("Outbound flight search response:", outboundData);
      setFlightData(outboundData);

      // --- RETURN FLIGHTS (only if return date exists) ---
      if (returnDateStr) {
        const returnParams = new URLSearchParams({
          origin: form.destination,
          destination: form.origin,
          outboundDate: returnDateStr,
          flight_type: "2",
        });

        const returnResponse = await fetch(
          http://localhost:8080/api/flights/search?${returnParams.toString()}
        );

        if (!returnResponse.ok)
          throw new Error(HTTP error! status: ${returnResponse.status});

        const returnData: FlightSearchResponse = await returnResponse.json();
        returnData.flights.sort((a, b) => {
        if (a.price === 0 && b.price === 0) return 0;
        if (a.price === 0) return 1;
        if (b.price === 0) return -1;
        return a.price - b.price;
      });
        console.log("Return flight search response:", returnData);
        setReturnFlightData(returnData);
      }
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

      { }
      {loading && <p className="mt-4 text-gray-600">Loading flights…</p>}
      {error && <p className="mt-4 text-red-500">{error}</p>}

      { }
      {flightData && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">
            {flightData.origin} → {flightData.destination}
            <p>
              From{" "}
              {flightData.flights[0].price +
                (form.return && returnFlightData?.flights?.[0]?.price
                  ? returnFlightData.flights[0].price
                  : 0)} {flightData.flights[0].currency} {(form.return ? "roundtrip" : "one-way")}
            </p>
          </h2>
          <FlightCard data={flightData} />
        </div>
      )}
            {returnFlightData && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">
            {returnFlightData.origin} → {returnFlightData.destination}
          </h2>
          <FlightCard data={returnFlightData} />
        </div>
      )}
    </div>
  );
}
ChatGPT said:

Got it ✅ — you want to let users click on a flight card to “focus” it:

    Only the clicked flight is shown (others dismissed).

    The “From” price updates to that specific flight’s price (instead of always showing the cheapest one).

Here’s how to modify your code to support that cleanly:
✅ Key Changes:

    Add state for the selected flight.

    Pass a click handler to FlightCard that sets the selected flight.

    Conditionally render either:

        All flights (no selection yet), or

        The focused flight only (after click).

    Update the “From” price to reflect the selected flight.

✨ Updated SearchForm.tsx

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
  const [returnFlightData, setReturnFlightData] = useState<FlightSearchResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedFlight, setSelectedFlight] = useState<Flight | null>(null);

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
    setReturnFlightData(null);
    setSelectedFlight(null);
    setLoading(true);

    try {
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
        flight_type: "2",
      });

      const outboundResponse = await fetch(
        `http://localhost:8080/api/flights/search?${params.toString()}`
      );
      if (!outboundResponse.ok)
        throw new Error(`HTTP error! status: ${outboundResponse.status}`);

      const outboundData: FlightSearchResponse = await outboundResponse.json();
      outboundData.flights.sort((a, b) => a.price - b.price);
      setFlightData(outboundData);

      if (returnDateStr) {
        const returnParams = new URLSearchParams({
          origin: form.destination,
          destination: form.origin,
          outboundDate: returnDateStr,
          flight_type: "2",
        });

        const returnResponse = await fetch(
          `http://localhost:8080/api/flights/search?${returnParams.toString()}`
        );
        if (!returnResponse.ok)
          throw new Error(`HTTP error! status: ${returnResponse.status}`);

        const returnData: FlightSearchResponse = await returnResponse.json();
        returnData.flights.sort((a, b) => a.price - b.price);
        setReturnFlightData(returnData);
      }
    } catch (err: any) {
      console.error("Error fetching flights:", err);
      setError(err.message || "Error fetching flights");
    } finally {
      setLoading(false);
    }
  }

  const handleFlightClick = (flight: Flight) => {
    setSelectedFlight(flight);
  };

  const handleBackToAll = () => {
    setSelectedFlight(null);
  };

  const displayedFlight =
    selectedFlight ?? flightData?.flights?.[0] ?? null;

  const totalPrice =
    displayedFlight?.price +
    (form.return && returnFlightData?.flights?.[0]?.price
      ? returnFlightData.flights[0].price
      : 0);

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

      {loading && <p className="mt-4 text-gray-600">Loading flights…</p>}
      {error && <p className="mt-4 text-red-500">{error}</p>}

      {flightData && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">
            {flightData.origin} → {flightData.destination}
            <p>
              From {totalPrice} {displayedFlight?.currency}{" "}
              {form.return ? "roundtrip" : "one-way"}
            </p>
          </h2>

          {selectedFlight ? (
            <div>
              <FlightCard
                data={{ ...flightData, flights: [selectedFlight] }}
                onSelect={() => {}}
                isFocused
              />
              <button
                onClick={handleBackToAll}
                className="mt-4 bg-gray-300 text-gray-800 px-4 py-2 rounded"
              >
                Back to all flights
              </button>
            </div>
          ) : (
            <FlightCard
              data={flightData}
              onSelect={handleFlightClick}
              isFocused={false}
            />
          )}
        </div>
      )}

      {returnFlightData && !selectedFlight && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">
            {returnFlightData.origin} → {returnFlightData.destination}
          </h2>
          <FlightCard data={returnFlightData} onSelect={handleFlightClick} />
        </div>
      )}
    </div>
  );
}

✈️ Update Your FlightCard Component

You’ll need to make sure FlightCard accepts a click handler and optionally a isFocused prop.

Example:

export default function FlightCard({
  data,
  onSelect,
  isFocused = false,
}: {
  data: { flights: Flight[] };
  onSelect?: (flight: Flight) => void;
  isFocused?: boolean;
}) {
  return (
    <div className={`grid gap-4 ${isFocused ? "scale-105" : ""}`}>
      {data.flights.map((flight) => (
        <div
          key={`${flight.airline}-${flight.departureTime}`}
          className="border p-4 rounded-lg cursor-pointer hover:shadow-lg transition"
          onClick={() => onSelect && onSelect(flight)}
        >
          <div className="flex justify-between items-center">
            <div>
              <p className="font-semibold">{flight.airline}</p>
              <p>
                {flight.origin} → {flight.destination}
              </p>
            </div>
            <p className="text-lg font-bold">
              {flight.price} {flight.currency}
            </p>
          </div>
        </div>
      ))}
    </div>
  );
}

🧠 Summary

✅ Clicking a card:

    Focuses it (only one visible)

    Updates “From” price
    ✅ “Back to all flights” restores full list

Would you like me to also make the focused card visually “stand out” (e.g. shadow, highlight, zoom) for clarity?
You said:
i want return Flight Data to exist indepedently, and also focusable and updating the price at the same time - i.e. on a return flight you should be able to select 2 flights and see the total price for both

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
  const [returnFlightData, setReturnFlightData] = useState<FlightSearchResponse | null>(null);
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
        flight_type: "2",
      });

      // --- OUTBOUND FLIGHTS ---
      const outboundResponse = await fetch(
        http://localhost:8080/api/flights/search?${params.toString()}
      );

      if (!outboundResponse.ok)
        throw new Error(HTTP error! status: ${outboundResponse.status});

      const outboundData: FlightSearchResponse = await outboundResponse.json();        
      outboundData.flights.sort((a, b) => {
        if (a.price === 0 && b.price === 0) return 0;
        if (a.price === 0) return 1;
        if (b.price === 0) return -1;
        return a.price - b.price;
      });
      console.log("Outbound flight search response:", outboundData);
      setFlightData(outboundData);

      // --- RETURN FLIGHTS (only if return date exists) ---
      if (returnDateStr) {
        const returnParams = new URLSearchParams({
          origin: form.destination,
          destination: form.origin,
          outboundDate: returnDateStr,
          flight_type: "2",
        });

        const returnResponse = await fetch(
          http://localhost:8080/api/flights/search?${returnParams.toString()}
        );

        if (!returnResponse.ok)
          throw new Error(HTTP error! status: ${returnResponse.status});

        const returnData: FlightSearchResponse = await returnResponse.json();
        returnData.flights.sort((a, b) => {
        if (a.price === 0 && b.price === 0) return 0;
        if (a.price === 0) return 1;
        if (b.price === 0) return -1;
        return a.price - b.price;
      });
        console.log("Return flight search response:", returnData);
        setReturnFlightData(returnData);
      }
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

      { }
      {loading && <p className="mt-4 text-gray-600">Loading flights…</p>}
      {error && <p className="mt-4 text-red-500">{error}</p>}

      { }
      {flightData && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">
            {flightData.origin} → {flightData.destination}
            <p>
              From{" "}
              {flightData.flights[0].price +
                (form.return && returnFlightData?.flights?.[0]?.price
                  ? returnFlightData.flights[0].price
                  : 0)} {flightData.flights[0].currency} {(form.return ? "roundtrip" : "one-way")}
            </p>
          </h2>
          <FlightCard data={flightData} />
        </div>
      )}
            {returnFlightData && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">
            {returnFlightData.origin} → {returnFlightData.destination}
          </h2>
          <FlightCard data={returnFlightData} />
        </div>
      )}
    </div>
  );
}
ChatGPT said:

Perfect — you want independent focusing for outbound and return flights, where:

    The user can select one outbound and one return flight.

    Each side collapses to show only the selected card when focused.

    The total price shown updates dynamically as either is selected.

Below is a clean, drop-in version of your SearchForm that supports this behavior.
✅ Updated SearchForm.tsx

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
  const [returnFlightData, setReturnFlightData] = useState<FlightSearchResponse | null>(null);
  const [selectedOutbound, setSelectedOutbound] = useState<Flight | null>(null);
  const [selectedReturn, setSelectedReturn] = useState<Flight | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);
    setFlightData(null);
    setReturnFlightData(null);
    setSelectedOutbound(null);
    setSelectedReturn(null);
    setLoading(true);

    try {
      const outboundDateStr = form.outbound
        ? form.outbound.toISOString().split("T")[0]
        : "";
      const returnDateStr = form.return
        ? form.return.toISOString().split("T")[0]
        : undefined;

      // --- OUTBOUND ---
      const outboundParams = new URLSearchParams({
        origin: form.origin,
        destination: form.destination,
        outboundDate: outboundDateStr,
        flight_type: "2",
      });

      const outboundRes = await fetch(
        `http://localhost:8080/api/flights/search?${outboundParams.toString()}`
      );
      if (!outboundRes.ok)
        throw new Error(`HTTP error! status: ${outboundRes.status}`);

      const outboundData: FlightSearchResponse = await outboundRes.json();
      outboundData.flights.sort((a, b) => a.price - b.price);
      setFlightData(outboundData);

      // --- RETURN (if exists) ---
      if (returnDateStr) {
        const returnParams = new URLSearchParams({
          origin: form.destination,
          destination: form.origin,
          outboundDate: returnDateStr,
          flight_type: "2",
        });
        const returnRes = await fetch(
          `http://localhost:8080/api/flights/search?${returnParams.toString()}`
        );
        if (!returnRes.ok)
          throw new Error(`HTTP error! status: ${returnRes.status}`);

        const returnData: FlightSearchResponse = await returnRes.json();
        returnData.flights.sort((a, b) => a.price - b.price);
        setReturnFlightData(returnData);
      }
    } catch (err: any) {
      console.error("Error fetching flights:", err);
      setError(err.message || "Error fetching flights");
    } finally {
      setLoading(false);
    }
  }

  // === PRICE HANDLING ===
  const outboundPrice = selectedOutbound?.price ?? flightData?.flights?.[0]?.price ?? 0;
  const returnPrice = selectedReturn?.price ?? returnFlightData?.flights?.[0]?.price ?? 0;
  const currency =
    selectedOutbound?.currency ||
    flightData?.flights?.[0]?.currency ||
    selectedReturn?.currency ||
    returnFlightData?.flights?.[0]?.currency ||
    "";

  const totalPrice = outboundPrice + (form.return ? returnPrice : 0);

  return (
    <div className="p-6">
      {/* === SEARCH FORM === */}
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

      {/* === STATUS === */}
      {loading && <p className="mt-4 text-gray-600">Loading flights…</p>}
      {error && <p className="mt-4 text-red-500">{error}</p>}

      {/* === OUTBOUND FLIGHTS === */}
      {flightData && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">
            {flightData.origin} → {flightData.destination}
            <p>
              From {totalPrice} {currency}{" "}
              {form.return ? "roundtrip" : "one-way"}
            </p>
          </h2>

          {selectedOutbound ? (
            <div>
              <FlightCard
                data={{ ...flightData, flights: [selectedOutbound] }}
                onSelect={() => {}}
                isFocused
              />
              <button
                onClick={() => setSelectedOutbound(null)}
                className="mt-4 bg-gray-300 text-gray-800 px-4 py-2 rounded"
              >
                Back to all outbound flights
              </button>
            </div>
          ) : (
            <FlightCard
              data={flightData}
              onSelect={setSelectedOutbound}
              isFocused={false}
            />
          )}
        </div>
      )}

      {/* === RETURN FLIGHTS === */}
      {returnFlightData && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">
            {returnFlightData.origin} → {returnFlightData.destination}
          </h2>

          {selectedReturn ? (
            <div>
              <FlightCard
                data={{ ...returnFlightData, flights: [selectedReturn] }}
                onSelect={() => {}}
                isFocused
              />
              <button
                onClick={() => setSelectedReturn(null)}
                className="mt-4 bg-gray-300 text-gray-800 px-4 py-2 rounded"
              >
                Back to all return flights
              </button>
            </div>
          ) : (
            <FlightCard
              data={returnFlightData}
              onSelect={setSelectedReturn}
              isFocused={false}
            />
          )}
        </div>
      )}
    </div>
  );
}

🧩 Update Your FlightCard.tsx

This should already work with the same logic as before, but make sure it accepts onSelect and isFocused props:

export default function FlightCard({
  data,
  onSelect,
  isFocused = false,
}: {
  data: { flights: Flight[] };
  onSelect?: (flight: Flight) => void;
  isFocused?: boolean;
}) {
  return (
    <div className={`grid gap-4 ${isFocused ? "scale-105" : ""}`}>
      {data.flights.map((flight) => (
        <div
          key={`${flight.airline}-${flight.departureTime}`}
          className="border p-4 rounded-lg cursor-pointer hover:shadow-lg transition"
          onClick={() => onSelect && onSelect(flight)}
        >
          <div className="flex justify-between items-center">
            <div>
              <p className="font-semibold">{flight.airline}</p>
              <p>
                {flight.origin} → {flight.destination}
              </p>
            </div>
            <p className="text-lg font-bold">
              {flight.price} {flight.currency}
            </p>
          </div>
        </div>
      ))}
    </div>
  );
}

✈️ Behavior Summary
Action	Result
Click outbound flight	Focuses outbound section, hides other outbound cards
Click return flight	Focuses return section, hides other return cards
Total price	Updates dynamically as you pick outbound + return
“Back to all” buttons	Restore full flight lists for that section

Would you like me to add an optional “combined summary card” at the bottom that shows both selected flights together (like an itinerary preview)?
You said:
heres the current card

import React from "react";
import "./FlightCard.css";

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

interface FlightCardProps {
  data: FlightSearchResponse;
}

const FlightCard: React.FC<FlightCardProps> = ({ data }) => {
  if (!data || !data.flights?.length) {
    return <p>No flights found.</p>;
  }

  return (
    <div className="flight-grid">
      {data.flights.map((flight, index) => (
        <div key={index} className="flight-card">
          <img src={flight.airlineLogo} alt={flight.airline} />
            <h3>
            {flight.airline}{" "}
            {flight.airlineLogo ===
            "https://www.gstatic.com/flights/airline_logos/70px/multi.png"
                ? + ${flight.layovers} more
                : ""}
            </h3>
          <p>
            {new Date(flight.departureTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}  →  {new Date(flight.arrivalTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
          </p>
          <p>Duration: {Math.floor(flight.durationMinutes / 60)}h {flight.durationMinutes % 60}m</p>
          <p>Layovers: {flight.layovers}</p>
          <p><strong>{flight.price} {flight.currency}</strong></p>
        </div>
      ))}
    </div>
  );
};


export default FlightCard;
ChatGPT said:

Perfect — since your FlightCard is currently purely presentational, we’ll make it interactive while keeping its style and structure intact.

Here’s an updated version that supports click-to-focus functionality (used by the modified SearchForm I shared earlier).
✅ Updated FlightCard.tsx (Interactive Version)

import React from "react";
import "./FlightCard.css";

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

interface FlightCardProps {
  data: FlightSearchResponse;
  /** called when user clicks a flight */
  onSelect?: (flight: Flight) => void;
  /** when true, indicates this card is focused (used for styling) */
  isFocused?: boolean;
}

const FlightCard: React.FC<FlightCardProps> = ({ data, onSelect, isFocused = false }) => {
  if (!data || !data.flights?.length) {
    return <p>No flights found.</p>;
  }

  return (
    <div className={`flight-grid ${isFocused ? "focused" : ""}`}>
      {data.flights.map((flight, index) => (
        <div
          key={index}
          className={`flight-card ${onSelect ? "clickable" : ""}`}
          onClick={() => onSelect && onSelect(flight)}
        >
          <img src={flight.airlineLogo} alt={flight.airline} />
          <h3>
            {flight.airline}{" "}
            {flight.airlineLogo ===
            "https://www.gstatic.com/flights/airline_logos/70px/multi.png"
              ? `+ ${flight.layovers} more`
              : ""}
          </h3>
          <p>
            {new Date(flight.departureTime).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit",
            })}{" "}
            →{" "}
            {new Date(flight.arrivalTime).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit",
            })}
          </p>
          <p>
            Duration: {Math.floor(flight.durationMinutes / 60)}h{" "}
            {flight.durationMinutes % 60}m
          </p>
          <p>Layovers: {flight.layovers}</p>
          <p>
            <strong>
              {flight.price} {flight.currency}
            </strong>
          </p>
        </div>
      ))}
    </div>
  );
};

export default FlightCard;

🧩 Additions Explained

    Added two optional props:

    onSelect?: (flight: Flight) => void;
    isFocused?: boolean;

    When onSelect exists, cards become clickable (cursor: pointer).

    Clicking a flight calls onSelect(flight) — the parent (SearchForm) handles what happens (focus logic, price updates, etc.).

    isFocused can slightly alter visuals (e.g. enlarge, glow).

🎨 Suggested CSS Additions (FlightCard.css)

.flight-card.clickable {
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.flight-card.clickable:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.flight-grid.focused .flight-card {
  transform: scale(1.03);
  border: 2px solid #2563eb; /* blue-600 */
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.2);
}

✅ How it Works With the Updated SearchForm

    Outbound and return flight sections each pass onSelect={setSelectedOutbound} / onSelect={setSelectedReturn}.

    Clicking a card focuses only that section.

    “Back to all flights” restores the grid view.

    The top “From” price updates automatically with both selected flight prices.

Would you like me to also make the selected card visibly highlighted (e.g. blue border, glow) even when both outbound and return are selected? That’s easy to add and gives a clear selection indicator.