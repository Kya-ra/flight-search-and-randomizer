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
  const [outboundMin, setOutboundMin] = useState(0);
  const [outboundMax, setOutboundMax] = useState(0);
  const [returnMin, setReturnMin] = useState(0);
  const [returnMax, setReturnMax] = useState(0);


  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  function buildFlexDates(base: Date | null, min: number, max: number): string[] {
    if (!base) return [];
    const dates: string[] = [];

    for (let diff = min; diff <= max; diff++) {
      const d = new Date(base);
      d.setDate(d.getDate() + diff);
      dates.push(d.toISOString().split("T")[0]);
    }

    return dates;
  }


  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);
    setFlightData(null);
    setReturnFlightData(null);
    setSelectedOutbound(null);
    setSelectedReturn(null);
    setRandomFlight(null);
    setLoading(true);

    try {
      const outboundDates = buildFlexDates(form.outbound, outboundMin, outboundMax);
      const returnDates = form.return
        ? buildFlexDates(form.return, returnMin, returnMax)
        : [];

      const outboundResponses = await Promise.all(
        outboundDates.map((date) => {
          const params = new URLSearchParams({
            origin: form.origin,
            destination: form.destination,
            outboundDate: date,
            flight_type: "2",
          });

          return fetch(`http://localhost:8080/api/flights/search?${params}`);
        })
      );

      const outboundJsons: FlightSearchResponse[] =
        await Promise.all(outboundResponses.map((r) => r.json()));

      const mergedOutbound: FlightSearchResponse = {
        ...outboundJsons[0],
        flights: outboundJsons.flatMap((d) => d.flights),
        totalResults: outboundJsons.reduce((a, b) => a + b.totalResults, 0),
        cheapestPrice: Math.min(...outboundJsons.map((d) => d.cheapestPrice)),
      };

      mergedOutbound.flights.sort((a, b) => a.price - b.price);
      setFlightData(mergedOutbound);

      if (returnDates.length > 0) {
        const returnResponses = await Promise.all(
          returnDates.map((date) => {
            const params = new URLSearchParams({
              origin: form.destination,
              destination: form.origin,
              outboundDate: date,
              flight_type: "2",
            });

            return fetch(`http://localhost:8080/api/flights/search?${params}`);
          })
        );

        const returnJsons: FlightSearchResponse[] =
          await Promise.all(returnResponses.map((r) => r.json()));

        const mergedReturn: FlightSearchResponse = {
          ...returnJsons[0],
          flights: returnJsons.flatMap((d) => d.flights),
          totalResults: returnJsons.reduce((a, b) => a + b.totalResults, 0),
          cheapestPrice: Math.min(...returnJsons.map((d) => d.cheapestPrice)),
        };

        mergedReturn.flights.sort((a, b) => a.price - b.price);
        setReturnFlightData(mergedReturn);
      }
    } catch (err: any) {
      console.error(err);
      setError(err.message);
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
    setLoading(true);
    setError(null);

    try {
      let url = "http://localhost:8080/api/flights/random";

      if (form.outbound) {
        const startDate = form.outbound.toISOString().split("T")[0];
        const endDate = form.return
          ? form.return.toISOString().split("T")[0]
          : startDate;

        url += `?origin=${form.origin}&destination=${form.destination}&startDate=${startDate}&endDate=${endDate}`;
      }

      const res = await fetch(url);

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
        <button type="button" onClick={() => window.location.reload()} className="bg-blue-600 text-white py-2 rounded">
          Reset
        </button>
      </form>

   <div className="flex flex-col gap-2">
    <span className="font-semibold">{"\u2003"}{"\u2003"}{"\u2003"}{"\u2003"}{"\u2003"}{"\u2003"}Outbound Flex{"\u2003"}Return Flex</span>

    <div className="flex items-center gap-2">
      <span>Days Before:{"\u2003"}</span>
      <button
        type="button"
        disabled={outboundMin <= -2}
        onClick={() => setOutboundMin(outboundMin - 1)}
        className="px-2 bg-gray-200 rounded"
      >-</button>
      <span>{outboundMin}</span>
      <button
        type="button"
        disabled={outboundMin >= 0}
        onClick={() => setOutboundMin(outboundMin + 1)}
        className="px-2 bg-gray-200 rounded"
      >+</button>
      <span>&nbsp;</span>
      <button
        type="button"
        disabled={returnMin <= -2}
        onClick={() => setReturnMin(returnMin - 1)}
        className="px-2 bg-gray-200 rounded"
      >-</button>
      <span>{returnMin}</span>
      <button
        type="button"
        disabled={returnMin >= 0}
        onClick={() => setReturnMin(returnMin + 1)}
        className="px-2 bg-gray-200 rounded"
      >+</button>
    </div>

    <div className="flex items-center gap-2">
      <span>Days After:{"\u2003"}{"\u2003"}</span>
      <button
        type="button"
        disabled={outboundMax <= 0}
        onClick={() => setOutboundMax(outboundMax - 1)}
        className="px-2 bg-gray-200 rounded"
      >-</button>
      <span>{outboundMax}</span>
      <button
        type="button"
        disabled={outboundMax >= 2}
        onClick={() => setOutboundMax(outboundMax + 1)}
        className="px-2   bg-gray-200 rounded"
      >+</button>
      <span>&nbsp;</span>
      <button
        type="button"
        disabled={returnMax <= 0}
        onClick={() => setReturnMax(returnMax - 1)}
        className="px-2 bg-gray-200 rounded"
      >-</button>
      <span>{returnMax}</span>
      <button
        type="button"
        disabled={returnMax >= 2}
        onClick={() => setReturnMax(returnMax + 1)}
        className="px-2 bg-gray-200 rounded"
      >+</button>
    </div>
  </div>





      {}
      {loading && <p className="mt-4 text-gray-600">Loading flights…</p>}
      {error && <p className="mt-4 text-red-500">{error}</p>}

      {randomFlight !== null && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">Random flight suggestion</h2>
          {randomFlight ? (
            <FlightCard
              data={{
                origin: randomFlight.origin,
                destination: randomFlight.destination,
                outboundDate: randomFlight?.departureTime?.split(" ")[0] ?? "",
                returnDate: "",
                totalResults: 1,
                cheapestPrice: randomFlight.price,
                flights: [randomFlight],
              }}
              onSelect={() => {}}
              isFocused={true}
            />
          ) : (
            <p className="mt-4 text-gray-600">No random flights found for your criteria.</p>
          )}
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
