package ru.skypro.homework.exception;

/**
 * Исключение, выбрасываемое при попытке найти несуществующий комментарий.
 */
public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
