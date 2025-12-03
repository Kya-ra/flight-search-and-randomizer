package ie.tcd.scss.flight_scout.controller;

import ie.tcd.scss.flight_scout.model.Flight;
import ie.tcd.scss.flight_scout.model.FlightSearchResponse;
import ie.tcd.scss.flight_scout.service.FlightSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;




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
     * @param departureToken Departure token (optional) - from selected outbound flight to get return options
     * @param adults Number of adults (optional, default: 1)
     * @param children Number of children (optional, default: 0)
     * @param travelClass Class of service (optional, default: 1) - 1=Economy, 2=Premium, 3=Business, 4=First
     * @param maxBudget Maximum price filter (optional) - applied client-side
     * @param currency Currency code (optional, default: "EUR")
     * @return FlightSearchResponse with matching flights
     */
    @GetMapping("/search") 
    public ResponseEntity<?> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam Integer flight_type,
            @RequestParam String outboundDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam(required = false) String departureToken,
            @RequestParam(required = false, defaultValue = "1") Integer adults,
            @RequestParam(required = false, defaultValue = "0") Integer children,
            @RequestParam(required = false, defaultValue = "1") Integer travelClass,
            @RequestParam(required = false) Integer maxBudget,
            @RequestParam(required = false, defaultValue = "EUR") String currency) {

        try {
            FlightSearchResponse response = flightSearchService.searchFlights(
                    origin,
                    destination,
                    outboundDate,
                    flight_type,
                    returnDate,
                    departureToken,
                    adults,
                    children,
                    travelClass,
                    maxBudget,
                    currency
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // This is where "Could not recognise origin 'xyz'..." ends up
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }


    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Flight search service is running");
    }

    /**
     * Search for flights across a flexible date range and budget.
     *
     * @param origin Airport code (required) - e.g., "DUB"
     * @param destination Airport code (required) - e.g., "BCN"
     * @param startDate Start of flexible date range (YYYY-MM-DD)
     * @param endDate End of flexible date range (YYYY-MM-DD)
     * @param maxBudget Maximum price filter (optional)
     * @param flightType Type of trip (optional, default = 2 → one-way)
     * @param currency Currency code (optional, default = "EUR")
     * @return FlightSearchResponse containing flights for all dates in range
     */
    @GetMapping("/flexible") 
    public ResponseEntity<?> getFlexibleFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Integer maxBudget,
            @RequestParam(defaultValue = "2") Integer flightType,
            @RequestParam(defaultValue = "EUR") String currency) {

        try {
            FlightSearchResponse response = flightSearchService.getFlexibleFlightOptions(
                    origin, destination, startDate, endDate, maxBudget, flightType, currency
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", ex.getMessage()));
        }
    }


     /**
     * Returns a completely random flight from a flexible date range.
     * @param tripType Type of themed destination (optional) - 1=Sun, 2=City, 3=Cold, 4/null=Random
     */
    @GetMapping("/random")
    public ResponseEntity<?> getRandomFlight(
            @RequestParam String origin,
            @RequestParam String date,
            @RequestParam int minusFlex,
            @RequestParam int plusFlex,
            @RequestParam(required = false) Integer tripType) {
        System.out.println("Random flight endpoint hit");
        Flight randomFlight = flightSearchService.getRandomFlight(origin, date, minusFlex, plusFlex, tripType);

        if (randomFlight == null) {
            return ResponseEntity.ok(Map.of("message", "No flights found"));
        }

        return ResponseEntity.ok(randomFlight);
    }
    
}
