package com.doctorhoai.bookinghotel.service.inter;

import com.doctorhoai.bookinghotel.dto.Response;
import com.doctorhoai.bookinghotel.entity.Booking;

public interface IBookingService {
    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);
    Response findBookingByConfirmationCode( String confirmationCode);
    Response getAllBookings();
    Response cancelBooking(Long bookingId);
}
