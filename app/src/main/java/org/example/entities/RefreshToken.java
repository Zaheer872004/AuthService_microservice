package org.example.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserInfo userInfo;
}

//@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "refresh_token")
//@Builder
//public class RefreshToken {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
////    @Column(name = "token_id")
//    private Long id;
//
//    private String token;
//
//    private Instant expiryDate;
//
//    // connecting one to one with user_info table
//    @OneToOne
//    @JoinColumn(name = "id", referencedColumnName = "user_id")
//    private UserInfo userInfo;
//
//
//
//
//}
