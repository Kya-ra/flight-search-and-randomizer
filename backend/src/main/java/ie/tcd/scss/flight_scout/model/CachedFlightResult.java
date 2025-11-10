package ie.tcd.scss.flight_scout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cached_flight_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CachedFlightResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @Column(nullable = false, length = 10)
    private String origin;

    @Column(nullable = false, length = 10)
    private String destination;

    @Column(nullable = false)
    private double price;

    @Column(length = 100)
    private String airline;

    @Column(nullable = false)
    private String departureTime;

    @Column(nullable = false)
    private String arrivalTime;

    @Column(nullable = false)
    private LocalDateTime fetchedAt;

    @ManyToOne
    @JoinColumn(name = "search_id", nullable = false)
    private SearchHistory search;
}
