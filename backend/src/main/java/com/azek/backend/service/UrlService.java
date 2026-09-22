package com.azek.backend.service;

import com.azek.backend.entity.Url;
import com.azek.backend.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    @Transactional
    public Url shortenUrl(String originalUrl){
        var existingUrl = urlRepository.findByOriginalUrl(originalUrl);
        if(existingUrl.isPresent()){
            return existingUrl.get();
        }

        String shortCode = "";
        do {
            shortCode = generateShortCode();

        }while(urlRepository.findByShortCode(shortCode).isPresent());

        return urlRepository.save(new Url(originalUrl, shortCode));
    }

    @Transactional
    public Optional<Url> getOriginalUrl(String shortCode){
        var url = urlRepository.findByShortCode(shortCode);
        if(url.isPresent()){
            var urlEntity = url.get();
            urlEntity.setClickCount(urlEntity.getClickCount() + 1);
            urlRepository.save(urlEntity);
            return Optional.of(urlEntity);
        }
        return Optional.empty();
    }

    public List<Url> getAllUrls() {
        return urlRepository.findAll();
    }

    public Url getUrlByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode).orElseThrow();
    }

    @Transactional
    public void deleteUrl(Long id) {
        urlRepository.deleteById(id);
    }

    private String generateShortCode() {
        var random = new Random();
        var sb = new StringBuilder(SHORT_CODE_LENGTH);

        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

}
