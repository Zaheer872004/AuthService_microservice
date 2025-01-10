package org.example.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.UserRole;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDTO {

//    private String username;
//    private String email;
//    private String role;
//    private String accessToken;
//    private String refreshToken;

    private String username;
    private String password;
    private String accessToken;
    private String token;


}


