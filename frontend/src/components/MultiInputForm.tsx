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
  const [randomFlight, setRandomFlight] = useState<Flight | null>(null);

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
      outboundData.flights.sort((a, b) => {
        if (a.price === 0 && b.price === 0) return 0;
        if (a.price === 0) return 1;
        if (b.price === 0) return -1;
        return a.price - b.price;
      });

      setFlightData(outboundData);

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
        returnData.flights.sort((a, b) => {
        if (a.price === 0 && b.price === 0) return 0;
        if (a.price === 0) return 1;
        if (b.price === 0) return -1;
        return a.price - b.price;
      });

        setReturnFlightData(returnData);
      }
    } catch (err: any) {
      console.error("Error fetching flights:", err);
      setError(err.message || "Error fetching flights");
    } finally {
      setLoading(false);
    }
  }

  const outboundPrice = selectedOutbound?.price ?? flightData?.flights?.[0]?.price ?? 0;
  const returnPrice = selectedReturn?.price ?? returnFlightData?.flights?.[0]?.price ?? 0;
  const currency =
    selectedOutbound?.currency ||
    flightData?.flights?.[0]?.currency ||
    selectedReturn?.currency ||
    returnFlightData?.flights?.[0]?.currency ||
    "";

  const totalPrice = outboundPrice + (form.return ? returnPrice : 0);

  const fetchRandomFlight = async () => {
  if (!form.origin || !form.destination || !form.outbound) {
    setError("Please fill in origin, destination, and outbound date first.");
    return;
  }

  setLoading(true);
  setError(null);

  try {
    const startDate = form.outbound.toISOString().split("T")[0];
    const endDate = form.return ? form.return.toISOString().split("T")[0] : startDate;

    const res = await fetch(
      `http://localhost:8080/api/flights/random?origin=${form.origin}&destination=${form.destination}&startDate=${startDate}&endDate=${endDate}`
    );

    if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
    const data: Flight = await res.json();
    setRandomFlight(data);
  } catch (err: any) {
    setError(err.message || "Error fetching random flight");
  } finally {
    setLoading(false);
  }
};

  return (
    <div className="p-6">
      {}
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
        <button
          type="button"
          className="bg-green-600 text-white py-2 rounded mt-2"
          onClick={fetchRandomFlight} >
          Get Random Flight
        </button>
      </form>

      {}
      {loading && <p className="mt-4 text-gray-600">Loading flights…</p>}
      {error && <p className="mt-4 text-red-500">{error}</p>}

      {randomFlight && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">Random flight suggestion</h2>
          <FlightCard
            data={{
              origin: randomFlight.origin,
              destination: randomFlight.destination,
              outboundDate: randomFlight.departureTime.split(" ")[0], // just the date
              returnDate: "",
              totalResults: 1,
              cheapestPrice: randomFlight.price,
              flights: [randomFlight],
            }}
            onSelect={() => {}}
            isFocused={true}
          />
        </div>
      )}

      {}
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

      {}
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
