package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService; // НОВОЕ

    @Override
    public User getUser(String email) {
        log.info("Получение информации о пользователе с email: {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        return userMapper.toUserDto(userEntity);
    }

    @Override
    @Transactional
    public UpdateUser updateUser(String email, UpdateUser updateUser) {
        log.info("Обновление информации о пользователе с email: {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        userMapper.updateUserEntityFromDto(updateUser, userEntity);
        userRepository.save(userEntity);

        return updateUser;
    }

    @Override
    @Transactional
    public void updatePassword(String email, NewPassword newPassword) {
        log.info("Обновление пароля для пользователя с email: {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), userEntity.getPassword())) {
            throw new AccessDeniedException("Неверный текущий пароль");
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(userEntity);

        log.info("Пароль успешно обновлен для пользователя: {}", email);
    }

    @Override
    @Transactional
    public void updateUserImage(String email, MultipartFile image) {
        log.info("Обновление аватара для пользователя с email: {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        // Обновляем изображение (старое удалится автоматически)
        String newImagePath = imageService.updateImage(userEntity.getImage(), image, "user", userEntity.getId());
        userEntity.setImage(newImagePath);
        userRepository.save(userEntity);
    }
}
