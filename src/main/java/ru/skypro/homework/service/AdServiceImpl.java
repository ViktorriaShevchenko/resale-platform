package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;

    @Override
    public Ads getAllAds() {
        log.info("Получение всех объявлений");

        List<AdEntity> entities = adRepository.findAll();
        List<Ad> adList = adMapper.toAdDtoList(entities);

        Ads ads = new Ads();
        ads.setCount(adList.size());
        ads.setResults(adList);

        return ads;
    }

    @Override
    public Ads getAdsByUser(String email) {
        log.info("Получение объявлений пользователя с email: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        List<AdEntity> entities = adRepository.findByAuthorId(user.getId());
        List<Ad> adList = adMapper.toAdDtoList(entities);

        Ads ads = new Ads();
        ads.setCount(adList.size());
        ads.setResults(adList);

        return ads;
    }

    @Override
    public ExtendedAd getAdById(Integer id) {
        log.info("Получение объявления с id: {}", id);

        AdEntity entity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        return adMapper.toExtendedAdDto(entity);
    }

    @Override
    @Transactional
    public Ad createAd(String email, CreateOrUpdateAd createOrUpdateAd, MultipartFile image) {
        log.info("Создание нового объявления пользователем с email: {}", email);

        UserEntity author = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        AdEntity adEntity = adMapper.toAdEntity(createOrUpdateAd);
        adEntity.setAuthor(author);

        adEntity.setImage("/ads/" + adEntity.getPk() + "/image");

        AdEntity savedAd = adRepository.save(adEntity);

        return adMapper.toAdDto(savedAd);
    }

    @Override
    @Transactional
    public Ad updateAd(Integer id, String email, CreateOrUpdateAd createOrUpdateAd) {
        log.info("Обновление объявления с id: {} пользователем с email: {}", id, email);

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        // Проверяем права на редактирование
        checkAdPermissions(adEntity, email);

        adMapper.updateAdEntityFromDto(createOrUpdateAd, adEntity);
        AdEntity updatedAd = adRepository.save(adEntity);

        return adMapper.toAdDto(updatedAd);
    }

    @Override
    @Transactional
    public void deleteAd(Integer id, String email) {
        log.info("Удаление объявления с id: {} пользователем с email: {}", id, email);

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        // Проверяем права на удаление
        checkAdPermissions(adEntity, email);

        adRepository.delete(adEntity);
    }

    @Override
    @Transactional
    public void updateAdImage(Integer id, String email, MultipartFile image) {
        log.info("Обновление картинки объявления с id: {} пользователем с email: {}", id, email);

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        // Проверяем права на редактирование
        checkAdPermissions(adEntity, email);

        adEntity.setImage("/ads/" + id + "/image");
        adRepository.save(adEntity);
    }

    private void checkAdPermissions(AdEntity adEntity, String email) {

        if (!adEntity.getAuthor().getEmail().equals(email)) {
            // Проверяем, может быть это админ?
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

            if (!"ADMIN".equals(user.getRole().name())) {
                throw new AccessDeniedException("Нет прав на редактирование этого объявления");
            }
        }
    }
}
