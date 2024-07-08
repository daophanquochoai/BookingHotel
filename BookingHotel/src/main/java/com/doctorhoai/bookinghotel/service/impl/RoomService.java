package com.doctorhoai.bookinghotel.service.impl;


import com.doctorhoai.bookinghotel.dto.Response;
import com.doctorhoai.bookinghotel.dto.RoomDTO;
import com.doctorhoai.bookinghotel.entity.Room;
import com.doctorhoai.bookinghotel.exception.OurException;
import com.doctorhoai.bookinghotel.repo.BookingRepository;
import com.doctorhoai.bookinghotel.repo.RoomRepository;
import com.doctorhoai.bookinghotel.service.inter.CloudinaryInter;
import com.doctorhoai.bookinghotel.service.inter.IRoomService;
import com.doctorhoai.bookinghotel.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
//    private final AwsS3Service awsS3Service;
    private final CloudinaryInter cloudinaryInter;

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();
        try{
            String image = cloudinaryInter.uploadFile(photo);
            Room room = new Room();
            room.setRoomPhotoUrl(image);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setRoomDescription(description);
            Room savedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200L);
            response.setMessage("Successful");
            response.setRoom(roomDTO);
        }
        catch ( Exception e ){
            response.setStatusCode(500L);
            response.setMessage("Error saving a room  " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findByDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {
        Response response = new Response();
        try{
            List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200L);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);
        }
        catch ( OurException o){

        }
        catch ( Exception e ){
            response.setStatusCode(500L);
            response.setMessage("Error get all room  " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteRoom(Long roomId) {
        Response response = new Response();
        try{
            roomRepository.findById(roomId).orElseThrow( () -> new OurException("Room Not Found"));
            roomRepository.deleteById(roomId);
            response.setStatusCode(200L);
            response.setMessage("Successful");
        }
        catch ( OurException o){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch ( Exception e ){
            response.setStatusCode(500L);
            response.setMessage("Error delete a room  " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();
        try{
            String image = null;
            if( !photo.isEmpty() && photo != null ){
                image = cloudinaryInter.uploadFile(photo);
            }
            Room room =  roomRepository.findById(roomId).orElseThrow( () -> new OurException("Room Not Found"));
            if( roomType != null ) room.setRoomType(roomType);
            if( description != null ) room.setRoomDescription(description);
            if( roomPrice != null ) room.setRoomPrice(roomPrice);
            if( image != null ) room.setRoomPhotoUrl(image);

            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);
            response.setStatusCode(200L);
            response.setMessage("successful");
            response.setRoom(roomDTO);
        }
        catch ( OurException o){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch ( Exception e ){
            response.setStatusCode(500L);
            response.setMessage("Error update a room  " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(Long roomId) {
        Response response = new Response();
        try{
            Room room =  roomRepository.findById(roomId).orElseThrow( () -> new OurException("Room Not Found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200L);
            response.setMessage("successful");
            response.setRoom(roomDTO);
        }
        catch ( OurException o){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch ( Exception e ){
            response.setStatusCode(500L);
            response.setMessage("Error get a room  " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response = new Response();
        try{
            List<Room> roomList = roomRepository.findAvailableRoomsByDatesAndTypes(checkInDate, checkOutDate, roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200L);
            response.setMessage("successful");
            response.setRoomList(roomDTOList);
        }
        catch ( Exception e ){
            response.setStatusCode(500L);
            response.setMessage("Error get available a room  " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();
        try{
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200L);
            response.setMessage("successful");
            response.setRoomList(roomDTOList);
        }
        catch ( OurException o){

        }
        catch ( Exception e ){
            response.setStatusCode(500L);
            response.setMessage("Error available all a room  " + e.getMessage());
        }
        return response;
    }
}
