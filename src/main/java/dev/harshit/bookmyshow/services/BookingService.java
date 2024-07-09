package dev.harshit.bookmyshow.services;

import dev.harshit.bookmyshow.exceptions.UserNotFoundException;
import dev.harshit.bookmyshow.models.*;
import dev.harshit.bookmyshow.repositories.BookingRepository;
import dev.harshit.bookmyshow.repositories.ShowRepository;
import dev.harshit.bookmyshow.repositories.ShowSeatRepository;
import dev.harshit.bookmyshow.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private UserRepository userRepository;
    private ShowRepository showRepository;
    private ShowSeatRepository showSeatRepository;
    private PriceCalculatorService priceCalculatorService;
    private BookingRepository bookingRepository;

    public BookingService(
            UserRepository userRepository,
            ShowRepository showRepository,
            ShowSeatRepository showSeatRepository,
            PriceCalculatorService priceCalculatorService,
            BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.priceCalculatorService = priceCalculatorService;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)    // Annotation to take the lock
    public Booking issueTicket(Long userId, Long showId, List<Long> showSeatIds) throws UserNotFoundException {

        /*
        * ------ TAKE LOCK HERE FOR NOW ------
        * 1. Get the user details from the DB
        * 2. Get the show details
        *  ------- TAKE LOCK ------------
        * 3. Get the show seats from the DB
        * 4. Check if the seats are available or not
        * 5. If not, throw an error
        * 6. If yes, mark the status of the seats as BLOCKED and update the timer
        *  --------- END LOCK ------------
        * 7. Create booking object
        * 8. Return to controller
        *  ------ END LOCK HERE FOR NOW
        * */

        // 1. Get the user from the UserRepository
        Optional<User> userOptional = userRepository.findById(userId);
        // Check if user is not present
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();   // throw an exception
        }

        // If user is present, get the user details
        User bookedBy = userOptional.get();

        // 2. Get the Show from the ShowRepository
        Optional<Show> showOptional = showRepository.findById(showId);
        if (showOptional.isEmpty()) {
            throw new RuntimeException();   // TODO: Add ShowNotFoundException
        }

        Show show = showOptional.get();

        // 3. Get the show seats from the showSeatRepository
        List<ShowSeat> showSeats = showSeatRepository.findAllById(showSeatIds);

        // 4. Check if the seats are available or not
        for (ShowSeat showSeat : showSeats) {
            if (!(showSeat.getShowSeatStatus().equals(ShowSeatStatus.AVAILABLE) ||
                    (showSeat.getShowSeatStatus().equals(ShowSeatStatus.BLOCKED) &&
                            Duration.between(showSeat.getBlockedAt().toInstant(),
                                    new Date().toInstant()).toMinutes() > 15))) {
                // 5.throw an error
                throw new RuntimeException();
            }
        }

        // 6. If seats are available, update shoe seat status BLOCKED, update date and save to DB
        for (ShowSeat showSeat : showSeats) {
            showSeat.setShowSeatStatus(ShowSeatStatus.BLOCKED);
            showSeat.setBlockedAt(new Date());
            showSeatRepository.save(showSeat);
        }

        // 7. Create booking object
        Booking booking = new Booking();
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setUser(bookedBy);
        booking.setBookedAt(new Date());
        booking.setShowSeats(showSeats);
        booking.setShow(show);
        booking.setAmount(priceCalculatorService.calculatePrice(showSeats, show));

        return bookingRepository.save(booking);
    }
}
