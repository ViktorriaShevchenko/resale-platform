package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdMapper adMapper;

    @InjectMocks
    private AdServiceImpl adService;

    private UserEntity author;
    private AdEntity adEntity;
    private CreateOrUpdateAd createOrUpdateAd;

    @BeforeEach
    void setUp() {
        author = new UserEntity();
        author.setId(1);
        author.setEmail("author@test.com");
        author.setRole(Role.USER);

        adEntity = new AdEntity();
        adEntity.setPk(100);
        adEntity.setTitle("Тестовое объявление");
        adEntity.setPrice(1000);
        adEntity.setAuthor(author);

        createOrUpdateAd = new CreateOrUpdateAd();
        createOrUpdateAd.setTitle("Новое название");
        createOrUpdateAd.setPrice(2000);
        createOrUpdateAd.setDescription("Новое описание");
    }

    @Test
    void updateAd_WhenUserIsAuthor_ShouldUpdate() {
        // Arrange
        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));

        Ad updatedAdDto = new Ad();
        updatedAdDto.setPk(100);
        updatedAdDto.setTitle("Новое название");
        updatedAdDto.setPrice(2000);
        updatedAdDto.setAuthor(1);

        when(adRepository.save(any(AdEntity.class))).thenReturn(adEntity);
        when(adMapper.toAdDto(any(AdEntity.class))).thenReturn(updatedAdDto);

        // Act
        Ad result = adService.updateAd(100, "author@test.com", createOrUpdateAd);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.getPk());
        assertEquals("Новое название", result.getTitle());
        assertEquals(2000, result.getPrice());

        verify(adRepository).findById(100);
        verify(adMapper).updateAdEntityFromDto(eq(createOrUpdateAd), any(AdEntity.class));
        verify(adRepository).save(any(AdEntity.class));
        verify(adMapper).toAdDto(any(AdEntity.class));

        // Проверяем, что userRepository НЕ вызывался
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void updateAd_WhenUserIsAdmin_ShouldUpdate() {
        // Arrange
        UserEntity admin = new UserEntity();
        admin.setId(2);
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);

        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        Ad updatedAdDto = new Ad();
        updatedAdDto.setPk(100);
        updatedAdDto.setTitle("Новое название");
        updatedAdDto.setPrice(2000);
        updatedAdDto.setAuthor(1);

        when(adRepository.save(any(AdEntity.class))).thenReturn(adEntity);
        when(adMapper.toAdDto(any(AdEntity.class))).thenReturn(updatedAdDto);

        // Act
        Ad result = adService.updateAd(100, "admin@test.com", createOrUpdateAd);

        // Assert
        assertNotNull(result);
        verify(adRepository).findById(100);
        verify(userRepository).findByEmail("admin@test.com");
    }

    @Test
    void updateAd_WhenUserIsNotAuthorAndNotAdmin_ShouldThrowAccessDenied() {
        // Arrange
        UserEntity otherUser = new UserEntity();
        otherUser.setId(3);
        otherUser.setEmail("other@test.com");
        otherUser.setRole(Role.USER);

        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> adService.updateAd(100, "other@test.com", createOrUpdateAd));

        verify(adRepository).findById(100);
        verify(userRepository).findByEmail("other@test.com");
        verify(adRepository, never()).save(any());
    }

    @Test
    void updateAd_WhenAdNotFound_ShouldThrowAdNotFoundException() {
        // Arrange
        when(adRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AdNotFoundException.class,
                () -> adService.updateAd(999, "author@test.com", createOrUpdateAd));

        verify(adRepository).findById(999);
        verify(userRepository, never()).findByEmail(anyString());
        verify(adRepository, never()).save(any());
    }

    @Test
    void updateAd_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> adService.updateAd(100, "unknown@test.com", createOrUpdateAd));
    }
}
