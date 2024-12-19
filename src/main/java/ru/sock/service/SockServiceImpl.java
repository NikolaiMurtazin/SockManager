package ru.sock.service;

import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sock.dto.SockDto;
import ru.sock.mapper.SockMapper;
import ru.sock.model.Sock;
import ru.sock.repository.SockRepository;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SockServiceImpl implements SockService {
    private final SockRepository sockRepository;
    private final SockMapper sockMapper;

    @Transactional
    public void registerIncome(SockDto sockDto) {
        log.info("Начата регистрация прихода носков: {}", sockDto);

        Sock sock = sockRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage())
                .stream().findFirst().orElse(new Sock());
        sock.setQuantity(sock.getQuantity() + sockDto.getQuantity());
        sockMapper.toSockDto(sockRepository.save(sock));

        log.info("Приход носков успешно зарегистрирован: {}", sock);
    }

    @Transactional
    public void registerOutcome(SockDto sockDto) {
        log.info("Начата регистрация расхода носков: {}", sockDto);

        Sock sock = sockRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage())
                .stream().findFirst()
                .orElseThrow(() -> {
                    log.error("Носки с цветом '{}' и процентом хлопка '{}' не найдены.", sockDto.getColor(), sockDto.getCottonPercentage());
                    return new IllegalArgumentException("На складе недостаточно носков");
                });

        if (sock.getQuantity() < sockDto.getQuantity()) {
            log.error("Недостаточно носков на складе. Доступно: {}, требуется: {}", sock.getQuantity(), sockDto.getQuantity());
            throw new IllegalArgumentException("На складе недостаточно носков");
        }

        sock.setQuantity(sock.getQuantity() - sockDto.getQuantity());
        sockRepository.save(sock);

        log.info("Расход носков успешно зарегистрирован: {}", sock);
    }

    public List<SockDto> filterSocks(String color, String operator, int cottonPercentage) {
        log.info("Фильтрация носков: цвет='{}', оператор='{}', процент хлопка={}", color, operator, cottonPercentage);

        List<Sock> socks;

        switch (operator) {
            case "moreThan" -> socks = sockRepository.findByColorAndCottonPercentageGreaterThan(color,
                    cottonPercentage);
            case "lessThan" -> socks = sockRepository.findByColorAndCottonPercentageLessThan(color, cottonPercentage);
            case "equal" -> socks = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage);
            default -> throw new IllegalArgumentException("Недопустимый оператор");
        };

        List<SockDto> sockDtos = socks.stream().map(sockMapper::toSockDto).collect(Collectors.toList());
        log.info("Фильтрация носков завершена. Найдено записей: {}", sockDtos.size());

        return sockDtos;
    }

    @Transactional
    public void batchUploadFromCSV(String filePath) {
        log.info("Начата загрузка носков из CSV файла: {}", filePath);

        int processedRows = 0;

        try (Reader reader = Files.newBufferedReader(Paths.get(filePath));
            CSVReader csvReader = new CSVReader(reader)) {

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String color = nextLine[0];
                int cottonPercentage = Integer.parseInt(nextLine[1]);
                int quantity = Integer.parseInt(nextLine[2]);
                if (color.isBlank() || cottonPercentage < 0 || cottonPercentage > 100 || quantity < 0) {
                    log.warn("Пропущена строка из CSV: некорректные данные. Строка: color='{}', cottonPercentage={}, quantity={}", color, cottonPercentage, quantity);
                    continue;
                }

                Sock sock = Sock.builder()
                        .color(color)
                        .cottonPercentage(cottonPercentage)
                        .quantity(quantity)
                        .build();
                sockRepository.save(sock);
                processedRows++;

                log.info("Добавлена партия носков из CSV: {}", sock);
            }
        } catch (Exception e) {
            log.error("Ошибка при загрузке носков из CSV файла: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при обработке CSV-файла: " + e.getMessage(), e);
        }

        if (processedRows == 0) {
            throw new RuntimeException("Все строки в CSV-файле некорректны");
        }

        log.info("Загрузка носков из CSV файла завершена.");
    }
}
