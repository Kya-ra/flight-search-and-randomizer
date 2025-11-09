package ie.tcd.scss.flight_scout.repository;

import ie.tcd.scss.flight_scout.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    
}
