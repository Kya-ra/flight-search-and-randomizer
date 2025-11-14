package ie.tcd.scss.flight_scout.service;

import ie.tcd.scss.flight_scout.model.User;
import ie.tcd.scss.flight_scout.model.UserPreference;
import ie.tcd.scss.flight_scout.repository.UserRepository;
import ie.tcd.scss.flight_scout.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing Users and their associated UserPreferences.
 * Consolidates user-related operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserManagementService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;


    /**
     * Save a new User to the database.
     * @param user the User to save
     * @return the saved User with generated ID
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Retrieve all Users from the database.
     * @return list of all Users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieve a User by its unique identifier.
     * @param id the User ID
     * @return Optional containing the User if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Update an existing User with new information.
     * @param id the ID of the User to update
     * @param updatedUser the User object containing updated information
     * @return the updated User
     * @throws IllegalArgumentException if User with given ID doesn't exist
     */
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setPreference(updatedUser.getPreference());
        existingUser.setSearches(updatedUser.getSearches());

        return userRepository.save(existingUser);
    }

    /**
     * Delete a User from the database.
     * @param id the ID of the User to delete
     * @throws IllegalArgumentException if User with given ID doesn't exist
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

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
    @Transactional(readOnly = true)
    public List<UserPreference> getAllUserPreferences() {
        return userPreferenceRepository.findAll();
    }

    /**
     * Retrieve a UserPreference by its unique identifier.
     * @param id the UserPreference ID
     * @return Optional containing the UserPreference if found, empty otherwise
     */
    @Transactional(readOnly = true)
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

    
    /**
     * Retrieve a User with their associated UserPreference loaded.
     * @param userId the User ID
     * @return Optional containing the User with preference if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithPreferences(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Update a User's preferences.
     * @param userId the User ID
     * @param updatedPreference the new preference data
     * @return the updated UserPreference
     * @throws IllegalArgumentException if User doesn't exist
     */
    public UserPreference updateUserPreferencesByUserId(Long userId, UserPreference updatedPreference) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserPreference existingPreference = user.getPreference();
        if (existingPreference == null) {
            updatedPreference.setUser(user);
            return userPreferenceRepository.save(updatedPreference);
        }

        existingPreference.setMaxBudget(updatedPreference.getMaxBudget());
        existingPreference.setPreferredClimate(updatedPreference.getPreferredClimate());
        existingPreference.setTripLength(updatedPreference.getTripLength());
        existingPreference.setHomeAirport(updatedPreference.getHomeAirport());
        existingPreference.setCurrency(updatedPreference.getCurrency());

        return userPreferenceRepository.save(existingPreference);
    }
}
