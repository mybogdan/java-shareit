package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.apache.catalina.connector.Request;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private Request request;
}
