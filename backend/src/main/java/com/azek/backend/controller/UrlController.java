package com.azek.backend.controller;

import com.azek.backend.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("urls", urlService.getAllUrls());
        return "home";
    }

    @PostMapping("/shorten")
    public String shortenUrl(@RequestParam("url") String originalUrl,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        try {
            if(!isValidUrl(originalUrl)) {
                redirectAttributes.addFlashAttribute("error", "Please enter a valid URL");
                return "redirect:/";
            }
            var url = urlService.shortenUrl(originalUrl);
            var baseUrl = getBaseUrl(request);
            var shortUrl = String.format("%s/%s", baseUrl, url.getShortCode());

            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("shortUrl", shortUrl);

        } catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Something went wrong");
        }
        return "redirect:/";
    }

    @GetMapping("/{shortCode}")
    public String redirectToOriginalUrl(@PathVariable String shortCode) {
        var originalUrl = urlService.getOriginalUrl(shortCode);
        return originalUrl.map(url -> "redirect:" + url.getOriginalUrl())
                .orElse("redirect:/");
    }

    @PostMapping("/delete/{id}")
    public String deleteUrl(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            urlService.deleteUrl(id);
            redirectAttributes.addFlashAttribute("message", "URL deleted successfully ");
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Something went wrong");
        }
        return "redirect:/";
    }

    private String getBaseUrl(HttpServletRequest request) {
        return String.format("%s://%s:%s", request.getScheme(), request.getServerName(), request.getServerPort());
    }

    private boolean isValidUrl(String url) {
        return Objects.nonNull(url) && url.matches("^(http|https)://.*$");
    }

}
