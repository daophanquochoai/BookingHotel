package com.doctorhoai.bookinghotel.service.impl;

import com.doctorhoai.bookinghotel.dto.BookingDTO;
import com.doctorhoai.bookinghotel.dto.Response;
import com.doctorhoai.bookinghotel.entity.Booking;
import com.doctorhoai.bookinghotel.entity.Room;
import com.doctorhoai.bookinghotel.entity.User;
import com.doctorhoai.bookinghotel.exception.OurException;
import com.doctorhoai.bookinghotel.repo.BookingRepository;
import com.doctorhoai.bookinghotel.repo.RoomRepository;
import com.doctorhoai.bookinghotel.repo.UserRepository;
import com.doctorhoai.bookinghotel.service.inter.IBookingService;
import com.doctorhoai.bookinghotel.service.inter.IRoomService;
import com.doctorhoai.bookinghotel.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final IRoomService iRoomService;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();
        try{
            if( bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
                throw new IllegalArgumentException("Check in date must come after check out date");
            }
            Room room = roomRepository.findById(roomId).orElseThrow( () -> new OurException("Room not found") );
            User user = userRepository.findById(userId).orElseThrow( () -> new OurException("User not found") );

            List<Booking> existingBokings = room.getBookings();
            if( !roomAvailable( bookingRequest, existingBokings) ){
                throw new OurException("Room not available for selected date range");
            }
            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            log.info("{}", bookingRequest);
            bookingRepository.save(bookingRequest);
            response.setStatusCode(200L);
            response.setMessage("successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);
        }
        catch ( OurException o){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage("Error saving booking " + e.getMessage());
        }
        return response;
    }

    private boolean roomAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                ||
                                (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate()) && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                ||
                                (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                ||
                                (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                ||
                                (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate()) && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                ||
                                (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate()) && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }

    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();
        try{
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow( () -> new OurException("Booking not found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
            response.setStatusCode(200L);
            response.setMessage("successful");
            response.setBooking(bookingDTO);
        }
        catch ( OurException o){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage("Error getting booking " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllBookings() {
        Response response = new Response();
        try{
            List<Booking> bookings = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDTO> bookingDTOS = Utils.mapBookingListEntityToBookingListDTO(bookings);
            response.setStatusCode(200L);
            response.setMessage("successful");
            response.setBookingList(bookingDTOS);
        }
        catch ( OurException o){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage("Error getting all Booking " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();
        try{
            bookingRepository.findById(bookingId).orElseThrow( () -> new OurException("Booking Does not exists"));
            bookingRepository.deleteById(bookingId);
            response.setStatusCode(200L);
            response.setMessage("successful");
        }
        catch ( OurException o){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage("Error cancel a booking " + e.getMessage());
        }
        return response;
    }
}
