package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;

public interface AdService {

    Ads getAllAds();
    Ads getAdsByUser(String email);
    ExtendedAd getAdById(Integer id);
    Ad createAd(String email, CreateOrUpdateAd createOrUpdateAd, MultipartFile image);
    Ad updateAd(Integer id, String email, CreateOrUpdateAd createOrUpdateAd);
    void deleteAd(Integer id, String email);
    void updateAdImage(Integer id, String email, MultipartFile image);
}
