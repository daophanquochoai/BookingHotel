package com.doctorhoai.bookinghotel.service.impl;

import com.doctorhoai.bookinghotel.dto.LoginRequest;
import com.doctorhoai.bookinghotel.dto.Response;
import com.doctorhoai.bookinghotel.dto.UserDTO;
import com.doctorhoai.bookinghotel.entity.User;
import com.doctorhoai.bookinghotel.exception.OurException;
import com.doctorhoai.bookinghotel.repo.UserRepository;
import com.doctorhoai.bookinghotel.service.inter.IUserService;
import com.doctorhoai.bookinghotel.utils.JWTUtils;
import com.doctorhoai.bookinghotel.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public Response register(User user) {
        Response response = new Response();
        try {
            if( user.getRole() == null || user.getRole().isBlank()){
                user.setRole("USER");
            }
            if(userRepository.existsByEmail(user.getEmail())){
                throw new OurException(user.getEmail() + " already exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);
            response.setStatusCode(200L);
            response.setUser(userDTO);
        }
        catch (OurException o){
            response.setStatusCode(400L);
            response.setMessage(o.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage( "Error Occured During User Registration " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow( () -> new OurException("user not found"));
            var token = jwtUtils.generateToken(user);
            response.setStatusCode(200L);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 day");
            response.setMessage("successfully");
        }
        catch (OurException o){
            response.setStatusCode(400L);
            response.setMessage(o.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage( "Error Occured During User Login " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllUsers() {
        Response response = new Response();
        try{
            List<User> userList = userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);
            response.setStatusCode(200L);
            response.setMessage("Successful");
            response.setUserList(userDTOList);
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage( "Error get all user" + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response response = new Response();
        try{
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow( () -> new OurException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);
            response.setStatusCode(200L);
            response.setMessage("Successful");
            response.setUser(userDTO);
        }
        catch ( OurException o ){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage("Error getting user booking history : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteUser(String userId) {
        Response response = new Response();
        try{
            userRepository.findById(Long.valueOf(userId)).orElseThrow( () -> new OurException("User not found"));
            userRepository.deleteById(Long.valueOf(userId));
            response.setStatusCode(200L);
            response.setMessage("Successful");
        }
        catch ( OurException o ){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage("Error delete  users : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();
        try{
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow( () -> new OurException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200L);
            response.setMessage("Successful");
            response.setUser(userDTO);
        }
        catch ( OurException o ){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage("Error getting a user : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response = new Response();
        try{
            User user = userRepository.findByEmail(email).orElseThrow( () -> new OurException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200L);
            response.setMessage("Successful");
            response.setUser(userDTO);
        }
        catch ( OurException o ){
            response.setStatusCode(404L);
            response.setMessage(o.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500L);
            response.setMessage("Error my info : " + e.getMessage());
        }
        return response;
    }
}
