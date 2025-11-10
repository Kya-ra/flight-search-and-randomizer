package ie.tcd.scss.flight_scout.service;

import ie.tcd.scss.flight_scout.model.User;
import ie.tcd.scss.flight_scout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository UserRepository;

    /**
     * Save a new User to the database.
     * @param User the User to save
     * @return the saved User with generated ID
     */
    public User saveUser(User User) {
        return UserRepository.save(User);
    }

    /**
     * Retrieve all Users from the database.
     * @return list of all Users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return UserRepository.findAll();
    }

    /**
     * Retrieve a User by its unique identifier.
     * @param id the User ID
     * @return Optional containing the User if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return UserRepository.findById(id);
    }

    /**
     * Update an existing User with new information.
     * @param id the ID of the User to update
     * @param updatedUser the User object containing updated information
     * @return the updated User
     * @throws IllegalArgumentException if User with given ID doesn't exist
     */
    public User updateUser(Long id, User updatedUser) {
        User existingUser = UserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setPreference(updatedUser.getPreference());
        existingUser.setSearches(updatedUser.getSearches());

        return UserRepository.save(existingUser);
    }

    /**
     * Delete a User from the database.
     * @param id the ID of the User to delete
     * @throws IllegalArgumentException if User with given ID doesn't exist
     */
    public void deleteUser(Long id) {
        if (!UserRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        UserRepository.deleteById(id);
    }
}
