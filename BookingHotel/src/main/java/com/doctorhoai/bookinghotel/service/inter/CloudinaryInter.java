package com.doctorhoai.bookinghotel.service.inter;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryInter {
    String uploadFile(MultipartFile multipartFile) throws IOException;
}
