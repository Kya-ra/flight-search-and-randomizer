package ie.tcd.scss.flight_scout.controller;

import ie.tcd.scss.flight_scout.domain.FlightSearchResponse;
import ie.tcd.scss.flight_scout.service.FlightSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")  
public class FlightController {

    @Autowired
    private FlightSearchService flightSearchService;

    /**
     * Search for flights with flexible parameters.
     *
     * @param origin Airport code (required) - e.g., "DUB"
     * @param destination Airport code (required) - e.g., "BCN"
     * @param flight_type Type of trip (required) - 1=Round-trip, 2=One-way, 3=Multi-city (Default : 1)
     * @param outboundDate Departure date (required) - format: YYYY-MM-DD
     * @param returnDate Return date (optional) - format: YYYY-MM-DD, omit for one-way
     * @param adults Number of adults (optional, default: 1)
     * @param children Number of children (optional, default: 0)
     * @param travelClass Class of service (optional, default: 1) - 1=Economy, 2=Premium, 3=Business, 4=First
     * @param maxBudget Maximum price filter (optional) - applied client-side
     * @param currency Currency code (optional, default: "EUR")
     * @return FlightSearchResponse with matching flights
     */
    @GetMapping("/search")
    public ResponseEntity<FlightSearchResponse> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam Integer flight_type,
            @RequestParam String outboundDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam(required = false, defaultValue = "1") Integer adults,
            @RequestParam(required = false, defaultValue = "0") Integer children,
            @RequestParam(required = false, defaultValue = "1") Integer travelClass,
            @RequestParam(required = false) Integer maxBudget,
            @RequestParam(required = false, defaultValue = "EUR") String currency) {

        FlightSearchResponse response = flightSearchService.searchFlights(
                origin,
                destination,
                outboundDate,
                flight_type,
                returnDate,
                adults,
                children,
                travelClass,
                maxBudget,
                currency
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get return flight options for a round-trip booking.
     * Use the departureToken from a selected outbound flight to fetch available return flights.
     *
     * Example: GET /api/flights/return?departureToken=WyJDalJJ...&origin=DUB&destination=BCN&returnDate=2025-12-22
     *
     * @param departureToken Token from the selected outbound flight (required)
     * @param origin Original departure airport (required) - e.g., "DUB"
     * @param destination Original arrival airport (required) - e.g., "BCN"
     * @param returnDate Return date (required) - format: YYYY-MM-DD
     * @return FlightSearchResponse containing available return flight options
     */
    @GetMapping("/return")
    public ResponseEntity<FlightSearchResponse> getReturnFlights(
            @RequestParam String departureToken,
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String returnDate) {

        FlightSearchResponse response = flightSearchService.getReturnFlights(
                departureToken, origin, destination, returnDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Flight search service is running");
    }
}
