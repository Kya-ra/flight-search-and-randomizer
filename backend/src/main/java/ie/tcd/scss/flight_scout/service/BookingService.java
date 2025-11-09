package ie.tcd.scss.flight_scout.service;

import ie.tcd.scss.flight_scout.model.Booking;
import ie.tcd.scss.flight_scout.repository.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    /**
     * Save a new Booking to the database.
     * @param booking the Booking to save
     * @return the saved Booking with generated ID
     */
    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    /**
     * Retrieve all Bookings from the database.
     * @return list of all Bookings
     */
    @Transactional
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Retrieve a Booking by its unique identifier.
     * @param id the Booking ID
     * @return Optional containing the Booking if found, empty otherwise
     */
    @Transactional
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    /**
     * Update an existing Booking with new information.
     * @param id the ID of the Booking to update
     * @param updatedBooking the Booking object containing updated information
     * @return the updated Booking
     * @throws IllegalArgumentException if Booking with given ID doesn't exist
     */
    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));

        existingBooking.setDate(updatedBooking.getDate());
        existingBooking.setStatus(updatedBooking.getStatus());
        existingBooking.setTotalPrice(updatedBooking.getTotalPrice());
        existingBooking.setUser(updatedBooking.getUser());
        

        return bookingRepository.save(existingBooking);
    }

    /**
     * Delete a Booking from the database.
     * @param id the ID of the Booking to delete
     * @throws IllegalArgumentException if Booking with given ID doesn't exist
     */
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new IllegalArgumentException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }
}

