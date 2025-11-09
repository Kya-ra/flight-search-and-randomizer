package ie.tcd.scss.flight_scout.service;

import ie.tcd.scss.flight_scout.model.UserPreference;
import ie.tcd.scss.flight_scout.repository.UserPreferenceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;

    /**
     * Save a new UserPreference to the database.
     * @param preference the UserPreference to save
     * @return the saved UserPreference with generated ID
     */
    public UserPreference saveUserPreference(UserPreference preference) {
        return userPreferenceRepository.save(preference);
    }

    /**
     * Retrieve all UserPreferences from the database.
     * @return list of all UserPreferences
     */
    @Transactional
    public List<UserPreference> getAllUserPreferences() {
        return userPreferenceRepository.findAll();
    }

    /**
     * Retrieve a UserPreference by its unique identifier.
     * @param id the UserPreference ID
     * @return Optional containing the UserPreference if found, empty otherwise
     */
    @Transactional
    public Optional<UserPreference> getUserPreferenceById(Long id) {
        return userPreferenceRepository.findById(id);
    }

    /**
     * Update an existing UserPreference with new information.
     * @param id the ID of the UserPreference to update
     * @param updatedPreference the UserPreference object containing updated information
     * @return the updated UserPreference
     * @throws IllegalArgumentException if UserPreference with given ID doesn't exist
     */
    public UserPreference updateUserPreference(Long id, UserPreference updatedPreference) {
        UserPreference existingPreference = userPreferenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserPreference not found with id: " + id));

        existingPreference.setMaxBudget(updatedPreference.getMaxBudget());
        existingPreference.setPreferredClimate(updatedPreference.getPreferredClimate());
        existingPreference.setTripLength(updatedPreference.getTripLength());
        existingPreference.setHomeAirport(updatedPreference.getHomeAirport());
        existingPreference.setCurrency(updatedPreference.getCurrency());
        existingPreference.setUser(updatedPreference.getUser());

        return userPreferenceRepository.save(existingPreference);
    }

    /**
     * Delete a UserPreference from the database.
     * @param id the ID of the UserPreference to delete
     * @throws IllegalArgumentException if UserPreference with given ID doesn't exist
     */
    public void deleteUserPreference(Long id) {
        if (!userPreferenceRepository.existsById(id)) {
            throw new IllegalArgumentException("UserPreference not found with id: " + id);
        }
        userPreferenceRepository.deleteById(id);
    }
}

