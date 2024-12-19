package ru.sock.service;

import ru.sock.dto.SockDto;

import java.util.List;

public interface SockService {
    void registerIncome(SockDto sockDto);

    void registerOutcome(SockDto sockDto);

    List<SockDto> filterSocks(String color, String operator, int cottonPercentage);

    void batchUploadFromCSV(String filePath);
}
