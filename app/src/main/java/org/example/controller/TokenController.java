package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.entities.RefreshToken;
import org.example.repository.RefreshTokenRepository;
import org.example.request.AuthRequestDTO;
import org.example.request.RefreshTokenRequestDTO;
import org.example.response.JwtResponseDTO;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Controller
public class TokenController
{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("auth/v1/login")
    public ResponseEntity AuthenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));

        System.out.println(authentication);

        System.out.println("-------------"+authentication.isAuthenticated());


        if(authentication.isAuthenticated()){

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());

            // here also have to create the access token
            String accessToken = jwtService.GenerateToken(authRequestDTO.getUsername());

            // Also need to save the refreshToken in the db
            refreshTokenRepository.save(refreshToken);


//            return new ResponseEntity<>(JwtResponseDTO.builder()
//                    .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
//                    .token(refreshToken.getToken())
//                    .build(), HttpStatus.OK);

            return new ResponseEntity<>(JwtResponseDTO
                    .builder()
                    .username(authRequestDTO.getUsername())
                    .accessToken(accessToken)
                    .token(refreshToken.getToken())
                    .build(), HttpStatus.OK);

        } else {
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("auth/v1/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));
    }


    @PostMapping("auth/v1/logout")
    public ResponseEntity<?> logout(@RequestBody String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.isEmpty()) {
                return new ResponseEntity<>("Refresh token is required", HttpStatus.BAD_REQUEST);
            }

            // Find the refresh token from the database and delete it
            refreshTokenService.deleteByToken(refreshToken);

            // Return a successful response
            return ResponseEntity.ok("Successfully logged out");
        } catch (Exception ex) {
            return new ResponseEntity<>("Error during logout", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
