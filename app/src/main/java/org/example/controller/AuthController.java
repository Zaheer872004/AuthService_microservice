package org.example.controller;


import lombok.AllArgsConstructor;
import org.example.entities.RefreshToken;
import org.example.entities.UserInfo;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepository;
import org.example.request.AuthRequestDTO;
import org.example.response.JwtResponseDTO;
import org.example.response.UserDetailRespone;

import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.example.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@AllArgsConstructor
@RestController
public class AuthController
{

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("auth/v1/signup")
    public ResponseEntity SignUp(@RequestBody UserInfoDto userInfoDto){
        try{
            Boolean isSignUped = userDetailsService.signupUser(userInfoDto); // problem is here
            System.out.println("Okay 1");
            if(Boolean.FALSE.equals(isSignUped)){
                return new ResponseEntity<>("Already Exist", HttpStatus.BAD_REQUEST);
            }
            System.out.println("Okay 2");

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());
            System.out.println("Okay 3");

            String jwtToken = jwtService.GenerateToken(userInfoDto.getUsername());
            System.out.println("Okay 4");

//            return new ResponseEntity<>(JwtResponseDTO
//                    .builder()
//                    .accessToken(jwtToken)
//                    .token(refreshToken.getToken())
//                    .build(), HttpStatus.OK);

            return new ResponseEntity<>(JwtResponseDTO
                    .builder()
                    .username(userInfoDto.getUsername())
                    .password(userInfoDto.getPassword())
                    .accessToken(jwtToken)
                    .token(refreshToken.getToken())
                    .build(), HttpStatus.OK);


        }catch (Exception ex){
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("auth/v1/userDetails")
//    public ResponseEntity<?> getUserDetails(Principal principal) {
//        try {
//            // Ensure the user is authenticated
//            if (principal == null) {
//                return new ResponseEntity<>("Unauthorized access", HttpStatus.UNAUTHORIZED);
//            }
//
//            // Extract username or other details from Principal
//            String username = principal.getName();
//
//            // Fetch user details based on the username
//            UserInfo user = userRepository.findByUsername(username);
//
//            if(user == null){
//                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//            }
//
//            // Prepare a response DTO with user details
//            UserDetailRespone userResponse = UserDetailRespone.builder()
//                    .username(user.getUsername())
//                    .password(user.getPassword())
//                    .roles(user.getRoles())
//                    .build();
//
//            return new ResponseEntity<>(userResponse, HttpStatus.OK);
//
//        } catch (Exception e) {
//            // Handle unexpected errors
//            return new ResponseEntity<>("Exception in User Service: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }




}
