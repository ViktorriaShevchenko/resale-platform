package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
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
    private final UserDetailsManager userDetailsManager;

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

        // Проверяем старый пароль
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), userEntity.getPassword())) {
            throw new AccessDeniedException("Неверный текущий пароль");
        }

        // Шифруем новый пароль и сохраняем
        userEntity.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(userEntity);

        // Обновляем пользователя в UserDetailsManager
        UserDetails userDetails = userDetailsManager.loadUserByUsername(email);
        userDetailsManager.updateUser(userDetails);
    }

    @Override
    @Transactional
    public void updateUserImage(String email, MultipartFile image) {
        log.info("Обновление аватара для пользователя с email: {}", email);

        try {
            UserEntity userEntity = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

            userEntity.setImage("/users/" + userEntity.getId() + "/image");
            userRepository.save(userEntity);

        } catch (Exception e) {
            log.error("Ошибка при сохранении аватара", e);
            throw new RuntimeException("Ошибка при сохранении аватара", e);
        }
    }
}
