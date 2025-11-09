package ie.tcd.scss.flight_scout.repository;

import ie.tcd.scss.flight_scout.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
}