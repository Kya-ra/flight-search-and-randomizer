package ie.tcd.scss.flight_scout.service;

import ie.tcd.scss.flight_scout.model.Destination;
import ie.tcd.scss.flight_scout.repository.DestinationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DestinationService {

    private final DestinationRepository destinationRepository;

    /**
     * Save a new Destination to the database.
     * @param destination the Destination to save
     * @return the saved Destination with generated ID
     */
    public Destination saveDestination(Destination destination) {
        return destinationRepository.save(destination);
    }

    /**
     * Retrieve all Destinations from the database.
     * @return list of all Destinations
     */
    @Transactional
    public List<Destination> getAllDestinations() {
        return destinationRepository.findAll();
    }

    /**
     * Retrieve a Destination by its unique identifier.
     * @param id the Destination ID
     * @return Optional containing the Destination if found, empty otherwise
     */
    @Transactional
    public Optional<Destination> getDestinationById(Long id) {
        return destinationRepository.findById(id);
    }

    /**
     * Update an existing Destination with new information.
     * @param id the ID of the Destination to update
     * @param updatedDestination the Destination object containing updated information
     * @return the updated Destination
     * @throws IllegalArgumentException if Destination with given ID doesn't exist
     */
    public Destination updateDestination(Long id, Destination updatedDestination) {
        Destination existingDestination = destinationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Destination not found with id: " + id));

        existingDestination.setCity(updatedDestination.getCity());
        existingDestination.setCountry(updatedDestination.getCountry());
        existingDestination.setAirportCode(updatedDestination.getAirportCode());
        existingDestination.setClimate(updatedDestination.getClimate());
        existingDestination.setAveragePrice(updatedDestination.getAveragePrice());

        return destinationRepository.save(existingDestination);
    }

    /**
     * Delete a Destination from the database.
     * @param id the ID of the Destination to delete
     * @throws IllegalArgumentException if Destination with given ID doesn't exist
     */
    public void deleteDestination(Long id) {
        if (!destinationRepository.existsById(id)) {
            throw new IllegalArgumentException("Destination not found with id: " + id);
        }
        destinationRepository.deleteById(id);
    }
}

