package ie.tcd.scss.flight_scout.service;

import ie.tcd.scss.flight_scout.model.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
public class FlightSearchService {

    @Value("${serpapi.api.key}")
    private String serpApiKey;

    private static final String SERP_API_URL = "https://serpapi.com/search";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Search for flights with optional parameters.
     *
     * @param origin Airport code (e.g., "DUB")
     * @param destination Airport code (e.g., "BCN")
     * @param outboundDate Format: YYYY-MM-DD
     * @param flight_type 1=Round-trip, 2=One-way, 3=Multi-city
     * @param returnDate Format: YYYY-MM-DD (null for one-way)
     * @param departureToken Token from selected outbound flight (optional, for return flights)
     * @param adults Number of adult passengers (default: 1)
     * @param children Number of child passengers (default: 0)
     * @param travelClass 1=Economy, 2=Premium, 3=Business, 4=First (default: 1)
     * @param maxBudget Maximum price filter (applied manually after call)
     * @param currency Currency code (default: "EUR")
     * @return FlightSearchResponse containing all matching flights
     */
    public FlightSearchResponse searchFlights(
            String origin,
            String destination,
            String outboundDate,
            Integer flight_type,
            String returnDate,
            String departureToken,
            Integer adults,
            Integer children,
            Integer travelClass,
            Integer maxBudget,
            String currency) {

        // Build SerpAPI URL with parameters
        String url = buildSerpApiUrl(origin, destination, outboundDate,  returnDate,
                                     departureToken, adults, children, travelClass, currency, flight_type);


        // Make API call
        String jsonResponse = restTemplate.getForObject(url, String.class);

        // Parse JSON response and convert to java object
        FlightSearchResponse response = parseFlightResponse(jsonResponse, origin, destination,
                                                            outboundDate, returnDate, flight_type);

        // Apply client-side max budget filter if provided
        if (maxBudget != null && response.getFlights() != null) {
            List<Flight> filteredFlights = response.getFlights().stream()
                    .filter(flight -> flight.getPrice() <= maxBudget)
                    .collect(Collectors.toList());
            response.setFlights(filteredFlights);
            response.setTotalResults(filteredFlights.size());

            // Update cheapest price after filtering
            if (!filteredFlights.isEmpty()) {
                response.setCheapestPrice(
                    filteredFlights.stream()
                        .mapToDouble(Flight::getPrice)
                        .min()
                        .orElse(0.0)
                );
            }
        }
        return response;
    }

    //Build the SerpAPI URL with all query parameters
    private String buildSerpApiUrl(String origin, String destination, String outboundDate,
                                   String returnDate, String departureToken, Integer adults, Integer children,
                                   Integer travelClass, String currency, Integer flight_type) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SERP_API_URL)
                .queryParam("engine", "google_flights")
                .queryParam("api_key", serpApiKey)
                .queryParam("departure_id", origin)
                .queryParam("arrival_id", destination)
                .queryParam("outbound_date", outboundDate)
                .queryParam("type", flight_type)
                .queryParam("currency", currency != null ? currency : "EUR")
                .queryParam("hl", "en");

        // Add return date for round-trip searches
        if(flight_type == 1){
            builder.queryParam("return_date", returnDate);
        }

        // Add optional passenger parameters
        if (adults != null && adults > 0) {
            builder.queryParam("adults", adults);
        }
        if (children != null && children > 0) {
            builder.queryParam("children", children);
        }

        // Add travel class if specified
        if (travelClass != null) {
            builder.queryParam("travel_class", travelClass);
        }

        // Build the URI - encode=false to prevent double-encoding
        String baseUrl = builder.build(false).toUriString();

        // Manually add departure token (Avoid double-encoding)
        if (departureToken != null && !departureToken.isEmpty()) {
            baseUrl += "&departure_token=" + departureToken;
        }

        return baseUrl;
    }


    // Parse SerpAPI JSON response into FlightSearchResponse
    private FlightSearchResponse parseFlightResponse(String jsonResponse, String origin,
                                                     String destination, String outboundDate,
                                                     String returnDate, Integer flightType) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            List<Flight> allFlights = new ArrayList<>();

            // Set trip type based on flight_type parameter: 1=round-trip, 2=one-way
            String tripType = (flightType != null && flightType == 1) ? "round-trip" : "one-way";

            // Parse best_flights array
            JsonNode bestFlights = root.path("best_flights");
            if (bestFlights.isArray()) {
                for (JsonNode flightNode : bestFlights) {
                    Flight flight = parseFlightFromNode(flightNode, origin, destination);
                    if (flight != null) {
                        flight.setTripType(tripType);
                        allFlights.add(flight);
                    }
                }
            }

            // Parse other_flights array
            JsonNode otherFlights = root.path("other_flights");
            if (otherFlights.isArray()) {
                for (JsonNode flightNode : otherFlights) {
                    Flight flight = parseFlightFromNode(flightNode, origin, destination);
                    if (flight != null) {
                        flight.setTripType(tripType);
                        allFlights.add(flight);
                    }
                }
            }

            // Parse price insights
            PriceInsights priceInsights = null;
            JsonNode priceInsightsNode = root.path("price_insights");
            if (!priceInsightsNode.isMissingNode()) {
                priceInsights = new PriceInsights();
                priceInsights.setLowestPrice(priceInsightsNode.path("lowest_price").asInt());
                priceInsights.setPriceLevel(priceInsightsNode.path("price_level").asText());

                // Parse typical price range
                JsonNode typicalRange = priceInsightsNode.path("typical_price_range");
                if (typicalRange.isArray() && typicalRange.size() >= 2) {
                    String range = typicalRange.get(0).asText() + " - " + typicalRange.get(1).asText();
                    priceInsights.setTypicalPriceRange(range);
                }
            }

            // Find cheapest price
            Double cheapestPrice = allFlights.stream()
                    .mapToDouble(Flight::getPrice)
                    .min()
                    .orElse(0.0);

            FlightSearchResponse response = new FlightSearchResponse();
            response.setOrigin(origin);
            response.setDestination(destination);
            response.setOutboundDate(outboundDate);
            response.setReturnDate(returnDate);
            response.setFlights(allFlights);
            response.setTotalResults(allFlights.size());
            response.setCheapestPrice(cheapestPrice);
            response.setPriceInsights(priceInsights);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            // Return empty response on error
            FlightSearchResponse errorResponse = new FlightSearchResponse();
            errorResponse.setOrigin(origin);
            errorResponse.setDestination(destination);
            errorResponse.setFlights(new ArrayList<>());
            errorResponse.setTotalResults(0);
            return errorResponse;
        }
    }

    /**
     * Parse individual flight node from SerpAPI response
     * For round-trip: shows outbound flight details, price includes both legs
     * For one-way: shows complete flight details
     */
    private Flight parseFlightFromNode(JsonNode flightNode, String origin, String destination) {
        try {
            Flight flight = new Flight();
            flight.setOrigin(origin);
            flight.setDestination(destination);
            flight.setPrice(flightNode.path("price").asDouble());
            flight.setCurrency("EUR");  // Default, could parse from response
            flight.setDepartureToken(flightNode.path("departure_token").asText());
            flight.setBookingToken(flightNode.path("booking_token").asText());

            // Parse carbon emissions
            JsonNode carbonNode = flightNode.path("carbon_emissions");
            if (!carbonNode.isMissingNode()) {
                flight.setCarbonEmissions(carbonNode.path("this_flight").asInt());
            }

            // Parse airline logo
            flight.setAirlineLogo(flightNode.path("airline_logo").asText(null));

            // Parse overnight flag
            if (!flightNode.path("overnight").isMissingNode()) {
                flight.setOvernight(flightNode.path("overnight").asBoolean());
            }

            // Parse extensions (amenities like wifi, legroom, power outlets)
            JsonNode extensionsNode = flightNode.path("extensions");
            if (extensionsNode.isArray()) {
                List<String> extensions = new ArrayList<>();
                for (JsonNode ext : extensionsNode) {
                    extensions.add(ext.asText());
                }
                flight.setExtensions(extensions);
            }

            // Parse flights array - get outbound flight details
            JsonNode flightsArray = flightNode.path("flights");
            if (flightsArray.isArray() && flightsArray.size() > 0) {
                JsonNode firstLeg = flightsArray.get(0);
                JsonNode lastLeg = flightsArray.get(flightsArray.size() - 1);

                // Basic flight info from first leg
                flight.setAirline(firstLeg.path("airline").asText());
                flight.setFlightNumber(firstLeg.path("flight_number").asText(null));
                flight.setAirplane(firstLeg.path("airplane").asText(null));
                flight.setTravelClass(firstLeg.path("travel_class").asText(null));

                flight.setDepartureTime(
                    firstLeg.path("departure_airport").path("date").asText() + " " +
                    firstLeg.path("departure_airport").path("time").asText()
                );
                flight.setArrivalTime(
                    lastLeg.path("arrival_airport").path("date").asText() + " " +
                    lastLeg.path("arrival_airport").path("time").asText()
                );

                // Calculate layovers (number of flight segments - 1)
                flight.setLayovers(flightsArray.size() - 1);

                // Use total_duration from API if available, otherwise sum individual legs
                Integer totalDuration = flightNode.path("total_duration").asInt(0);
                if (totalDuration > 0) {
                    flight.setDurationMinutes(totalDuration);
                } else {
                    int duration = 0;
                    for (JsonNode leg : flightsArray) {
                        duration += leg.path("duration").asInt(0);
                    }
                    flight.setDurationMinutes(duration);
                }
            }

            return flight;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Searches for the cheapest flights within a flexible date range and budget.
     *
     * @param origin Airport code (e.g. "DUB")
     * @param destination Airport code (e.g. "BCN")
     * @param startDate Start of the date range (YYYY-MM-DD)
     * @param endDate End of the date range (YYYY-MM-DD)
     * @param maxBudget Maximum budget in EUR
     * @param flightType 1=Round-trip, 2=One-way
     * @param currency Currency code (default: "EUR")
     * @return Combined list of flights within date range and under budget
     */

     public FlightSearchResponse getFlexibleFlightOptions(
        String origin,
        String destination,
        String startDate,
        String endDate,
        Integer maxBudget,
        Integer flightType,
        String currency) {

            List<Flight> allFlights = new ArrayList<>();

            try {
                // Convert string dates to LocalDate
                java.time.LocalDate start = java.time.LocalDate.parse(startDate);
                java.time.LocalDate end = java.time.LocalDate.parse(endDate);

                for (java.time.LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    String outboundDate = date.toString();
                    System.out.println("Searching flights for date: " + outboundDate);

                    FlightSearchResponse dailyResponse = searchFlights(
                            origin,
                            destination,
                            outboundDate,
                            flightType,
                            null,          
                            null,          
                            1,              
                            0,             
                            1,             
                            maxBudget,
                            currency
                    );

                    // Collect flights that fit within budget
                    if (dailyResponse.getFlights() != null) {
                        allFlights.addAll(dailyResponse.getFlights());
                    }

            } 
        } catch (Exception e) {
        e.printStackTrace();
        }
        // Create response with all results
        FlightSearchResponse flexibleResponse = new FlightSearchResponse();
        flexibleResponse.setOrigin(origin);
        flexibleResponse.setDestination(destination);
        flexibleResponse.setFlights(allFlights);
        flexibleResponse.setTotalResults(allFlights.size());

        // Calculate cheapest price in results
        if (!allFlights.isEmpty()) {
            double cheapest = allFlights.stream()
                    .mapToDouble(Flight::getPrice)
                    .min()
                    .orElse(0.0);
            flexibleResponse.setCheapestPrice(cheapest);
        }

        return flexibleResponse;
    }

    /**
    * Returns a completely random flight from all flights in the flexible date range.
    */
    public Flight getRandomFlight() {
        // Define some example origins and destinations
        String[] airports = {"DUB", "BCN", "LHR", "CDG", "AMS", "FRA", "MAD"};
        java.util.Random rand = new java.util.Random();

        // Pick random origin and destination
        String origin = airports[rand.nextInt(airports.length)];
        String destination;
        do {
            destination = airports[rand.nextInt(airports.length)];
        } while (destination.equals(origin));

        // Pick random dates 
        java.time.LocalDate start = java.time.LocalDate.now().plusDays(rand.nextInt(30));
        java.time.LocalDate end = start.plusDays(rand.nextInt(10) + 1); // flight duration 1–10 days
        String startDate = start.toString();
        String endDate = end.toString();
        Integer maxBudget = null;

        Integer flightType = 1;
        String currency = "EUR";

        // Get all flexible flight options
        FlightSearchResponse response = getFlexibleFlightOptions(
                origin, destination, startDate, endDate, maxBudget, flightType, currency
        );

        List<Flight> flights = response.getFlights();
        if (flights == null || flights.isEmpty()) {
            return null;
        }

        // Pick one completely at random
        int randomIndex = rand.nextInt(flights.size());
        return flights.get(randomIndex);
    }
            
}
