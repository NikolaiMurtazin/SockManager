package ru.sock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SockDto {

    @NotBlank(message = "Поле 'color' не должно быть пустым.")
    private String color;

    @Min(value = 0, message = "Процент хлопка (cottonPercentage) должен быть больше или равен 0.")
    @Max(value = 100, message = "Процент хлопка (cottonPercentage) не может быть больше 100.")
    private int cottonPercentage;

    @Min(value = 1, message = "Количество (quantity) должно быть не менее 1.")
    private int quantity;
}
