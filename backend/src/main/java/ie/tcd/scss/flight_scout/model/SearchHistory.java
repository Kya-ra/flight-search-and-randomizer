package ie.tcd.scss.flight_scout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "search_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long searchId;

    @Column(nullable = false, length = 10)
    private String origin;

    @Column(nullable = false)
    private double budget;

    @Column(nullable = false)
    private int passengers;

    @Column(nullable = false)
    private int tripLength;

    @Column(length = 20)
    private String preferredClimate;

    @Column(nullable = false)
    private LocalDateTime searchTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "search", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CachedFlightResult> cachedResults;
}
