package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

/**
 * Реализация сервиса для аутентификации и регистрации пользователей.
 * Обрабатывает логику входа в систему и создания новых учетных записей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Выполняет аутентификацию пользователя.
     * Проверяет существование пользователя и соответствие пароля.
     *
     * @param userName логин пользователя (email)
     * @param password пароль пользователя
     * @return true если аутентификация успешна, false если пользователь не найден или пароль неверен
     */
    @Override
    public boolean login(String userName, String password) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            return passwordEncoder.matches(password, userDetails.getPassword());
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    /**
     * Регистрирует нового пользователя.
     * Проверяет, что пользователь с таким email еще не существует,
     * создает новую запись в БД и шифрует пароль.
     *
     * @param register DTO с данными для регистрации
     * @return true если регистрация успешна, false если пользователь уже существует
     */
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
