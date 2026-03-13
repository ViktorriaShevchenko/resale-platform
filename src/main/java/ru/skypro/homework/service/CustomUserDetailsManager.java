package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void createUser(UserDetails user) {
        // Пользователи создаются через регистрацию, этот метод может быть пустым
        throw new UnsupportedOperationException("Используйте AuthService.register для создания пользователей");
    }

    @Override
    public void updateUser(UserDetails user) {
        // Обновляем только если пользователь существует
        UserEntity userEntity = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Обновляем пароль (остальные данные обновляются через UserService)
        userEntity.setPassword(user.getPassword());
        userRepository.save(userEntity);
    }

    @Override
    public void deleteUser(String username) {
        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        userRepository.delete(userEntity);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // Этот метод не используется, т.к. мы работаем через UserService
        throw new UnsupportedOperationException("Используйте UserService.updatePassword");
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsService.loadUserByUsername(username);
    }
}
