package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public boolean login(String userName, String password) {
        log.info("Попытка входа пользователя с email: {}", userName);

        UserEntity user = userRepository.findByEmail(userName).orElse(null);
        if (user == null) {
            log.warn("Пользователь с email {} не найден", userName);
            return false;
        }

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    @Transactional
    public boolean register(Register register) {
        log.info("Регистрация нового пользователя с email: {}", register.getUsername());

        // Проверяем, существует ли уже пользователь
        if (userRepository.findByEmail(register.getUsername()).isPresent()) {
            log.warn("Пользователь с email {} уже существует", register.getUsername());
            return false;
        }

        // Создаем пользователя через наш репозиторий
        UserEntity userEntity = userMapper.toUserEntity(register);
        userEntity.setPassword(passwordEncoder.encode(register.getPassword()));

        userRepository.save(userEntity);

        log.info("Пользователь с email {} успешно зарегистрирован", register.getUsername());
        return true;
    }
}
