package com.doctorhoai.bookinghotel.repo;

import com.doctorhoai.bookinghotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("select DISTINCT r.roomType from Room r")
    List<String> findByDistinctRoomTypes();
    @Query("SELECT r from Room r where r.roomType like %:roomType% AND r.id not in (select b.room.id from Booking b where ( b.checkInDate <= :checkOutDate and b.checkOutDate >= :checkInDate))")
    List<Room> findAvailableRoomsByDatesAndTypes(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
    @Query("SELECT r from Room r where r.id not in (select b.room.id from Booking b)")
    List<Room> getAllAvailableRooms();
}
