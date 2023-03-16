package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    private Long id;
    @Email
    private String email;
    private String name;
}
