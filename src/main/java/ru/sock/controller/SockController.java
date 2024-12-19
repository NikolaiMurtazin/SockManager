package ru.sock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.sock.dto.SockDto;
import ru.sock.service.SockService;

import javax.validation.Valid;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/socks")
public class SockController {
    private final SockService sockService;

    @PostMapping("/income")
    void registerIncome(@RequestBody @Valid SockDto sockDto) {
        sockService.registerIncome(sockDto);
    }

    @PostMapping("/outcome")
    void registerOutcome(@RequestBody @Valid SockDto sockDto) {
        sockService.registerOutcome(sockDto);
    }

    @GetMapping
    List<SockDto> filterSocks(@RequestParam String color,
                              @RequestParam String operator,
                              @RequestParam int cottonPercentage) {
        return sockService.filterSocks(color, operator, cottonPercentage);
    }

    @PostMapping("/batch")
    public void batchUpload(@RequestParam MultipartFile file) {
        try {
            String filePath = "закачанный_" + file.getOriginalFilename();
            file.transferTo(Paths.get(filePath));
            sockService.batchUploadFromCSV(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке файла: " + e.getMessage(), e);
        }
    }
}
