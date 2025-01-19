package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
//import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class UserInfo {

    @Id
    @Column(name = "user_id")
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private String userId;

    @Column(nullable = false,unique = true)
    private String username;

//    private String email;

    private String password;

//    private Optional<String> avatar;


    // connecting many to many with user_role table
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> roles = new HashSet<>();


}
