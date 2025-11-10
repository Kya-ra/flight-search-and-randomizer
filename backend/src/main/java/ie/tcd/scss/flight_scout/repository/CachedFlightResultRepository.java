package ie.tcd.scss.flight_scout.repository;

import ie.tcd.scss.flight_scout.model.CachedFlightResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachedFlightResultRepository extends JpaRepository<CachedFlightResult, Long> {
    
}
