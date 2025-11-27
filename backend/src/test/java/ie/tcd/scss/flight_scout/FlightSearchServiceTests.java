package ie.tcd.scss.flight_scout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ie.tcd.scss.flight_scout.model.Flight;
import ie.tcd.scss.flight_scout.model.FlightSearchResponse;
import ie.tcd.scss.flight_scout.service.FlightSearchService;


@SpringBootTest
class FlightSearchServiceTests {

    @Autowired
    private FlightSearchService flightSearchService;

    @Test
    void testFlexibleFlightSearch() {
        FlightSearchResponse response = flightSearchService.getFlexibleFlightOptions(
                "DUB",
                "BCN",
                "2025-06-01",
                "2025-06-03",
                150,
                2,
                "EUR"
        );

        assert(response != null);
        assert(response.getFlights() != null);

        System.out.println("Total flights found: " + response.getTotalResults());
        response.getFlights().forEach(f ->
            System.out.println(f.getDepartureTime() + " → " + f.getDestination() + " : " + f.getPrice())
        );
    }

    @Test
    void testRandomFlight() {
        Flight randomFlight = flightSearchService.getRandomFlight(
        "DUB",         
        "BCN",          
        "2025-12-01",   
        "2025-12-10",  
        null,          
        2,              
        "EUR"       
        );

        assertNotNull(randomFlight, "Random flight should not be null");
        assertEquals("DUB", randomFlight.getOrigin(), "Origin should be DUB");
        assertEquals("BCN", randomFlight.getDestination(), "Destination should be BCN");
        assertTrue(randomFlight.getPrice() > 0, "Price should be greater than 0");

        System.out.println("Random flight selected:");
        System.out.println(randomFlight.getDepartureTime() + " → " + randomFlight.getDestination() + " : " + randomFlight.getPrice());
    }
}
