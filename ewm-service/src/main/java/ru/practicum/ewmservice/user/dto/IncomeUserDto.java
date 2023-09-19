package ru.practicum.ewmservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class IncomeUserDto {

    @NotBlank(message = "Field: name. Error: must not be blank.")
    @Size(min = 2, max = 250)
    private String name;
    @Email(message = "Field: email. Error: not valid")
    @NotBlank(message = "Field: email. Error: must not be blank.")
    @Size(min = 6, max = 254)
    private String email;
}