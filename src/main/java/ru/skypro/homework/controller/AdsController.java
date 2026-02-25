package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@Tag(name = "Объявления")
public class AdsController {

    @Operation(
            summary = "Получение всех объявлений",
            responses = @ApiResponse(responseCode = "200", description = "OK")
    )
    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        Ads ads = new Ads();
        ads.setCount(0);
        ads.setResults(new ArrayList<>());
        return ResponseEntity.ok(ads);
    }

    @Operation(
            summary = "Добавление объявления",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(
            @RequestPart("properties") CreateOrUpdateAd properties,
            @RequestPart("image") MultipartFile image) {

        Ad ad = new Ad();
        ad.setPk(1);
        ad.setAuthor(1);
        ad.setPrice(properties.getPrice());
        ad.setTitle(properties.getTitle());
        ad.setImage("/ads/1/image");

        return ResponseEntity.status(HttpStatus.CREATED).body(ad);
    }

    @Operation(
            summary = "Получение информации об объявлении",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAds(@PathVariable Integer id) {
        ExtendedAd ad = new ExtendedAd();
        ad.setPk(id);
        ad.setTitle("Тест");
        ad.setPrice(1000);
        ad.setAuthorFirstName("Иван");
        return ResponseEntity.ok(ad);
    }

    @Operation(
            summary = "Удаление объявления",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAd(@PathVariable Integer id) {
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Обновление информации об объявлении",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAds(@PathVariable Integer id,
                                        @RequestBody CreateOrUpdateAd ad) {
        Ad updatedAd = new Ad();
        updatedAd.setPk(id);
        updatedAd.setAuthor(1);
        updatedAd.setPrice(ad.getPrice());
        updatedAd.setTitle(ad.getTitle());
        updatedAd.setImage("/ads/" + id + "/image");

        return ResponseEntity.ok(updatedAd);
    }

    @Operation(
            summary = "Получение объявлений авторизованного пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe() {
        Ads ads = new Ads();
        ads.setCount(0);
        ads.setResults(new ArrayList<>());
        return ResponseEntity.ok(ads);
    }

    @Operation(
            summary = "Обновление картинки объявления",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateImage(@PathVariable Integer id,
                                              @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(new byte[0]);
    }
}
