package ie.tcd.scss.flight_scout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Object representing a single flight option from Google Flights.
 * For round-trip searches: price includes both legs, but only shows outbound details.
 * Use departureToken to fetch return flight options via getReturnFlights endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    private String origin;
    private String destination;
    private Double price;
    private String currency;
    private String tripType;  // "one-way" or "round-trip"

    // Flight details 
    private String airline;
    private String airlineLogo;       // Airline logo URL
    private String flightNumber;      // e.g., "EI 123"
    private String airplane;          // Aircraft type (e.g., "Airbus A330")
    private String travelClass;       // e.g., "Economy", "Business"
    private String departureTime;
    private String arrivalTime;
    private Integer durationMinutes;
    private Integer layovers;
    private Boolean overnight;        // True if flight departs and arrives on different days

    // Amenities and extensions
    private List<String> extensions;  // e.g., ["Average legroom (31 in)", "Wi-Fi available", "Power outlets"]
    private Integer carbonEmissions;  // kg of CO2

    // Booking tokens
    private String departureToken;     // Use this to get return flight options
    private String bookingToken;       // For final booking link

}
