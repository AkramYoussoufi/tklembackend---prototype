package com.project.tklembackend.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private Long id;
    private String email;
    @Size(min = 8, message = "Minimum length must be 8 characters")
    private String password;
    private Boolean status;
}
