package ie.tcd.scss.flight_scout;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
