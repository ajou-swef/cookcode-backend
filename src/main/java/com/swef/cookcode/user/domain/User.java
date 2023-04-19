package com.swef.cookcode.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Getter
public class User {

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    private static final String NAME_REGEX = "[a-zA-Z가-힣]+( [a-zA-Z가-힣]+)*";
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MAX_NICKNAME_LENGTH = 10;
    private static final int MAX_PROFILEIMAGE_LENGTH = 300;

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = MAX_EMAIL_LENGTH)
    private String email;

    @Column(length = MAX_PROFILEIMAGE_LENGTH)
    private String profileImage = "";

    @Column(nullable = false, length = MAX_NICKNAME_LENGTH)
    private String nickname;

    private Boolean isQuit = false;

    @Enumerated(EnumType.STRING)
    private Authority authority = Authority.USER;

    @Enumerated(EnumType.STRING)
    private Status status = Status.VALID;
}
