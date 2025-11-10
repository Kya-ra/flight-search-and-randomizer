package ie.tcd.scss.flight_scout.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "destinations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long destinationId;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 10)
    private String airportCode;

    @Column(length = 20)
    private String climate;

    @Column(nullable = false)
    private double averagePrice;
}
