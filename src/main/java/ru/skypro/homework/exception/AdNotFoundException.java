package ru.skypro.homework.exception;

/**
 * Исключение, выбрасываемое при попытке найти несуществующее объявление.
 */
public class AdNotFoundException extends RuntimeException {
    public AdNotFoundException(String message) {
        super(message);
    }
}
