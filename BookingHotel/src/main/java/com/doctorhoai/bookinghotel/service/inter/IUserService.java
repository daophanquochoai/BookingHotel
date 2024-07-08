package com.doctorhoai.bookinghotel.service.inter;

import com.doctorhoai.bookinghotel.dto.LoginRequest;
import com.doctorhoai.bookinghotel.dto.Response;
import com.doctorhoai.bookinghotel.entity.User;

public interface IUserService {
    Response register(User user);
    Response login(LoginRequest loginRequest);
    Response getAllUsers();
    Response getUserBookingHistory(String userId);
    Response deleteUser(String userId);
    Response getUserById(String userId);
    Response getMyInfo(String email);
}
