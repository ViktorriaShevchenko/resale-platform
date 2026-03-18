package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.UserEntity;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 * Предоставляет методы для доступа к данным таблицы users.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
