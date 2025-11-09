package ie.tcd.scss.flight_scout.service;

import ie.tcd.scss.flight_scout.model.SearchHistory;
import ie.tcd.scss.flight_scout.repository.SearchHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

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
    @Transactional
    public List<SearchHistory> getAllSearchHistories() {
        return searchHistoryRepository.findAll();
    }

    /**
     * Retrieve a SearchHistory by its unique identifier.
     * @param id the SearchHistory ID
     * @return Optional containing the SearchHistory if found, empty otherwise
     */
    @Transactional
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
}

