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
  onSelect?: (flight: Flight) => void;
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
            {flight.airplane}
            </p>
            <p>
            <strong>
              {flight.price === 0
                ? "Unknown Price"
                : `${flight.price} ${flight.currency}`
              }
            </strong>
          </p>
        </div>
      ))}
    </div>
  );
};

export default FlightCard;
