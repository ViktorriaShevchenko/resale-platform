package ru.skypro.homework.exception;

/**
 * Исключение, выбрасываемое при попытке найти несуществующего пользователя.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
