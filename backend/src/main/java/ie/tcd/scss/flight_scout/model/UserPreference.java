package ie.tcd.scss.flight_scout.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferenceId;

    @Column(nullable = false)
    private double maxBudget;

    @Column(length = 20)
    private String preferredClimate;

    @Column(nullable = false)
    private int tripLength;

    @Column(length = 10)
    private String homeAirport;

    @Column(length = 10)
    private String currency;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}

