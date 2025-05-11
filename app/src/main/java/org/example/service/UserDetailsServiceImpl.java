package org.example.service;


import org.example.entities.UserInfo;
import org.example.model.UserInfoEvent;
import org.example.eventProducer.UserInfoProducer;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;


@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService
{

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserInfoProducer userInfoProducer;


    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {

        log.debug("Entering in loadUserByUsername Method...");
        UserInfo user = userRepository.findByUsername(username); // if we want to use the email then set findByUsername(email) and also in db as well
        if(user == null){
            log.error("Username not found: " + username);
            throw new UsernameNotFoundException("could not found user..!!");
        }
        log.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto){
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    public String getUserByUsername(String userName) {
        UserInfo user = userRepository.findByUsername(userName);
        System.out.println(user);
        return (user != null) ? user.getUserId() : null;
    }


    public Boolean signupUser(UserInfoDto userInfoDto){
        //        ValidationUtil.validateUserAttributes(userInfoDto);

        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword())); // setting and hashing the password here

        if(Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))){
            return false;
        }

        String userId = UUID.randomUUID().toString();

        userInfoDto.setUserId(userId); // Set userId in DTO

        UserInfo userInfo = new UserInfo(userId, userInfoDto.getUsername(), userInfoDto.getPassword(), new HashSet<>());
        userRepository.save(userInfo);
        System.out.println("User saved successfully..!!"+userInfo);
        System.out.println("User saved successfully..!!"+userInfoDto+"------------------------->"+userInfoDto.getUserId());
        // pushEventToQueue
        userInfoProducer.sendEventToKafka(userInfoEventToPublished(userInfoDto));

        System.out.println("done with sending the event to kafka");
        return true;
    }

    private UserInfoEvent userInfoEventToPublished(UserInfoDto userInfoDto){
        return UserInfoEvent.builder()
                .userId(userInfoDto.getUserId())
                .firstName(userInfoDto.getFirstName())
                .lasName(userInfoDto.getLastName())
                .email(userInfoDto.getEmail())
                .phoneNumber(userInfoDto.getPhoneNumber())
                .build();
    }

}