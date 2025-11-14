package ie.tcd.scss.flight_scout.service;

import ie.tcd.scss.flight_scout.model.CachedFlightResult;
import ie.tcd.scss.flight_scout.model.SearchHistory;
import ie.tcd.scss.flight_scout.repository.CachedFlightResultRepository;
import ie.tcd.scss.flight_scout.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service for search-related operations and managing SearchHistory and associated CachedFlightResults
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final CachedFlightResultRepository cachedFlightResultRepository;


    /**
     * Save a new SearchHistory to the database.
     * @param search the SearchHistory to save
     * @return the saved SearchHistory with generated ID
     */
    public SearchHistory saveSearchHistory(SearchHistory search) {
        return searchHistoryRepository.save(search);
    }

    /**
     * Retrieve all SearchHistories from the database.
     * @return list of all SearchHistories
     */
    @Transactional(readOnly = true)
    public List<SearchHistory> getAllSearchHistories() {
        return searchHistoryRepository.findAll();
    }

    /**
     * Retrieve a SearchHistory by its unique identifier.
     * @param id the SearchHistory ID
     * @return Optional containing the SearchHistory if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<SearchHistory> getSearchHistoryById(Long id) {
        return searchHistoryRepository.findById(id);
    }

    /**
     * Update an existing SearchHistory with new information.
     * @param id the ID of the SearchHistory to update
     * @param updatedSearch the SearchHistory object containing updated information
     * @return the updated SearchHistory
     * @throws IllegalArgumentException if SearchHistory with given ID doesn't exist
     */
    public SearchHistory updateSearchHistory(Long id, SearchHistory updatedSearch) {
        SearchHistory existingSearch = searchHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SearchHistory not found with id: " + id));

        existingSearch.setOrigin(updatedSearch.getOrigin());
        existingSearch.setBudget(updatedSearch.getBudget());
        existingSearch.setPassengers(updatedSearch.getPassengers());
        existingSearch.setTripLength(updatedSearch.getTripLength());
        existingSearch.setPreferredClimate(updatedSearch.getPreferredClimate());
        existingSearch.setSearchTime(updatedSearch.getSearchTime());
        existingSearch.setUser(updatedSearch.getUser());
        existingSearch.setCachedResults(updatedSearch.getCachedResults());

        return searchHistoryRepository.save(existingSearch);
    }

    /**
     * Delete a SearchHistory from the database.
     * @param id the ID of the SearchHistory to delete
     * @throws IllegalArgumentException if SearchHistory with given ID doesn't exist
     */
    public void deleteSearchHistory(Long id) {
        if (!searchHistoryRepository.existsById(id)) {
            throw new IllegalArgumentException("SearchHistory not found with id: " + id);
        }
        searchHistoryRepository.deleteById(id);
    }


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
    @Transactional(readOnly = true)
    public List<CachedFlightResult> getAllCachedFlightResults() {
        return cachedFlightResultRepository.findAll();
    }

    /**
     * Retrieve a CachedFlightResult by its unique identifier.
     * @param id the CachedFlightResult ID
     * @return Optional containing the CachedFlightResult if found, empty otherwise
     */
    @Transactional(readOnly = true)
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


    /**
     * Save a SearchHistory with its associated CachedFlightResults.
     * @param search the SearchHistory to save
     * @param results the list of CachedFlightResults to associate with this search
     * @return the saved SearchHistory with results
     */
    public SearchHistory saveSearchWithResults(SearchHistory search, List<CachedFlightResult> results) {
        SearchHistory savedSearch = searchHistoryRepository.save(search);

        if (results != null && !results.isEmpty()) {
            for (CachedFlightResult result : results) {
                result.setSearch(savedSearch);
                cachedFlightResultRepository.save(result);
            }
        }

        return savedSearch;
    }

    /**
     * Retrieve a SearchHistory with its cached results loaded.
     * @param searchId the SearchHistory ID
     * @return Optional containing the SearchHistory with results if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<SearchHistory> getSearchHistoryWithResults(Long searchId) {
        return searchHistoryRepository.findById(searchId);
    }

    /**
     * Retrieve all cached results for a specific search.
     * @param searchId the SearchHistory ID
     * @return list of CachedFlightResults for the given search
     */
    @Transactional(readOnly = true)
    public List<CachedFlightResult> getResultsBySearchId(Long searchId) {
        SearchHistory search = searchHistoryRepository.findById(searchId)
                .orElseThrow(() -> new IllegalArgumentException("SearchHistory not found with id: " + searchId));
        return search.getCachedResults();
    }
}
