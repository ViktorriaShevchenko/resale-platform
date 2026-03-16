package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final Path imageStoragePath;

    public String saveImage(MultipartFile file, String entityType, Integer entityId) {
        try {
            // 1. Валидация файла
            validateImage(file);

            // 2. Генерация уникального имени файла
            String filename = generateFilename(file, entityType, entityId);

            // 3. Сохранение файла
            Path targetPath = imageStoragePath.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Возвращаем URL для доступа к изображению
            return "/images/" + filename;

        } catch (IOException e) {
            log.error("Ошибка при сохранении изображения", e);
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // Извлекаем имя файла из URL
            String filename = extractFilenameFromUrl(imageUrl);
            Path filePath = imageStoragePath.resolve(filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Удалено изображение: {}", filename);
            }
        } catch (IOException e) {
            log.error("Ошибка при удалении изображения: {}", imageUrl, e);
        }
    }

    public String updateImage(String oldImageUrl, MultipartFile newFile,
                              String entityType, Integer entityId) {
        // Удаляем старое изображение, если оно есть
        deleteImage(oldImageUrl);

        // Сохраняем новое
        return saveImage(newFile, entityType, entityId);
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Разрешены только изображения");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5 MB
            throw new IllegalArgumentException("Размер файла не может превышать 5MB");
        }
    }

    private String generateFilename(MultipartFile file, String entityType, Integer entityId) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        // Формат: тип_entityId_uuid. расширение
        return String.format("%s_%d_%s.%s",
                entityType,
                entityId,
                UUID.randomUUID().toString().substring(0, 8),
                extension);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private String extractFilenameFromUrl(String imageUrl) {
        // URL формата: /images/filename.jpg
        if (imageUrl.startsWith("/images/")) {
            return imageUrl.substring(8); // Убираем "/images/"
        }
        return imageUrl;
    }
}
