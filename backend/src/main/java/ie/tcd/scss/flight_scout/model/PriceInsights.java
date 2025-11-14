package ie.tcd.scss.flight_scout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//Price insights from SerpAPI showing historical pricing data
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceInsights {

    private Integer lowestPrice;
    private String priceLevel;  // "low", "typical", "high"
    private String typicalPriceRange;  // e.g., "€90 - €150"

}
