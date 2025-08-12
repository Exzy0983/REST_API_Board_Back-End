package com.example.new_back_end.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String message;
}
