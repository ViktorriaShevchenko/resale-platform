package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;

public interface UserService {

    User getUser(String email);
    UpdateUser updateUser(String email, UpdateUser updateUser);
    void updatePassword(String email, NewPassword newPassword);
    void updateUserImage(String email, MultipartFile image);
}
