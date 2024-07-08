package com.doctorhoai.bookinghotel.controller;


import com.doctorhoai.bookinghotel.dto.Response;
import com.doctorhoai.bookinghotel.entity.Booking;
import com.doctorhoai.bookinghotel.service.inter.IBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final IBookingService bookingService;

    @PostMapping("/book-room/{roomId}/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Response> saveBookings(
            @PathVariable Long roomId,
            @PathVariable Long userId,
            @RequestBody Booking bookingRequest
            ){
        Response response = bookingService.saveBooking(roomId,userId,bookingRequest);
        return ResponseEntity.status(response.getStatusCode().intValue()).body(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllBookings(){
        Response response = bookingService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode().intValue()).body(response);
    }
    @GetMapping("/get-by-confirmation-code/{cofirmationCode}")
    public ResponseEntity<Response> getBookingByCofirmationCode( @PathVariable String cofirmationCode){
        Response response = bookingService.findBookingByConfirmationCode(cofirmationCode);
        return ResponseEntity.status(response.getStatusCode().intValue()).body(response);
    }

    @PostMapping("/cancel/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Response> cancelBooking(
            @PathVariable Long bookingId
    ){
        Response response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode().intValue()).body(response);
    }
}
