package ie.tcd.scss.flight_scout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


//Response object containing flight search results
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchResponse {

    private String origin;
    private String destination;
    private String outboundDate;
    private String returnDate;
    private Integer totalResults;
    private Double cheapestPrice;
    private List<Flight> flights;
    private PriceInsights priceInsights;

}
