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
          <h3>{flight.airline}</h3>
          <p>{flight.origin} → {flight.destination}</p>
          <p>
            Depart: {new Date(flight.departureTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
          </p>
          <p>
            Arrive: {new Date(flight.arrivalTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
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
