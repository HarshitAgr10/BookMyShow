package dev.harshit.bookmyshow.controllers;

import dev.harshit.bookmyshow.dtos.IssueTicketRequestDto;
import dev.harshit.bookmyshow.dtos.IssueTicketResponseDto;
import dev.harshit.bookmyshow.dtos.ResponseStatus;
import dev.harshit.bookmyshow.models.Booking;
import dev.harshit.bookmyshow.services.BookingService;
import org.springframework.stereotype.Controller;

@Controller
public class BookingController {
    private BookingService bookingService;

    public IssueTicketResponseDto issueTicket(IssueTicketRequestDto requestDto) {
        IssueTicketResponseDto responseDto = new IssueTicketResponseDto();
        Booking booking = null;

        try {
            booking = bookingService.issueTicket(
                    requestDto.getUserId(),
                    requestDto.getShowId(),
                    requestDto.getShowSeatIds()
            );
            responseDto.setBookingId(booking.getId());
            responseDto.setAmount(booking.getAmount());
        } catch (Exception ex) {
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
        }

        return responseDto;
    }
}
