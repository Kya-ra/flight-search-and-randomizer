package ie.tcd.scss.flight_scout.service;

import ie.tcd.scss.flight_scout.model.CachedFlightResult;
import ie.tcd.scss.flight_scout.repository.CachedFlightResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CachedFlightResultService {

    private final CachedFlightResultRepository cachedFlightResultRepository;

    /**
     * Save a new CachedFlightResult to the database.
     * @param result the CachedFlightResult to save
     * @return the saved CachedFlightResult with generated ID
     */
    public CachedFlightResult saveCachedFlightResult(CachedFlightResult result) {
        return cachedFlightResultRepository.save(result);
    }

    /**
     * Retrieve all CachedFlightResults from the database.
     * @return list of all CachedFlightResults
     */
    @Transactional
    public List<CachedFlightResult> getAllCachedFlightResults() {
        return cachedFlightResultRepository.findAll();
    }

    /**
     * Retrieve a CachedFlightResult by its unique identifier.
     * @param id the CachedFlightResult ID
     * @return Optional containing the CachedFlightResult if found, empty otherwise
     */
    @Transactional
    public Optional<CachedFlightResult> getCachedFlightResultById(Long id) {
        return cachedFlightResultRepository.findById(id);
    }

    /**
     * Update an existing CachedFlightResult with new information.
     * @param id the ID of the CachedFlightResult to update
     * @param updatedResult the CachedFlightResult object containing updated information
     * @return the updated CachedFlightResult
     * @throws IllegalArgumentException if CachedFlightResult with given ID doesn't exist
     */
    public CachedFlightResult updateCachedFlightResult(Long id, CachedFlightResult updatedResult) {
        CachedFlightResult existingResult = cachedFlightResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CachedFlightResult not found with id: " + id));

        existingResult.setOrigin(updatedResult.getOrigin());
        existingResult.setDestination(updatedResult.getDestination());
        existingResult.setPrice(updatedResult.getPrice());
        existingResult.setAirline(updatedResult.getAirline());
        existingResult.setDepartureTime(updatedResult.getDepartureTime());
        existingResult.setArrivalTime(updatedResult.getArrivalTime());
        existingResult.setFetchedAt(updatedResult.getFetchedAt());
        existingResult.setSearch(updatedResult.getSearch());

        return cachedFlightResultRepository.save(existingResult);
    }

    /**
     * Delete a CachedFlightResult from the database.
     * @param id the ID of the CachedFlightResult to delete
     * @throws IllegalArgumentException if CachedFlightResult with given ID doesn't exist
     */
    public void deleteCachedFlightResult(Long id) {
        if (!cachedFlightResultRepository.existsById(id)) {
            throw new IllegalArgumentException("CachedFlightResult not found with id: " + id);
        }
        cachedFlightResultRepository.deleteById(id);
    }
}

