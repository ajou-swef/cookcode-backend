package com.swef.cookcode.user.domain;

import static com.swef.cookcode.common.ErrorCode.INVALID_INPUT_VALUE;
import static com.swef.cookcode.common.ErrorCode.INVALID_LENGTH;
import static com.swef.cookcode.common.ErrorCode.MISSING_REQUEST_PARAMETER;
import static org.springframework.util.StringUtils.hasText;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Getter
public class User extends BaseEntity {

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    private static final String NICKNAME_REGEX = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{2,16}$";
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

    public User(String email, String nickname, Authority authority) {
        if (!hasText(email)) {
            throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
        }
        if (!hasText(nickname)) {
            throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
        }

        validateEmail(email);
        validateNickName(nickname);

        this.authority = authority;
        this.email = email;
        this.nickname = nickname;
    }

    public void updateAuthority(Authority authority) {
        this.authority = authority;
    }
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    private static void validateNickName(String name) {
        if (name.length() > MAX_NICKNAME_LENGTH) {
            throw new InvalidRequestException(INVALID_LENGTH);
        }
        if (!Pattern.matches(NICKNAME_REGEX, name)) {
            throw new InvalidRequestException(INVALID_INPUT_VALUE);
        }
    }

    private static void validateEmail(String email) {
        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new InvalidRequestException(INVALID_LENGTH);
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new InvalidRequestException(INVALID_INPUT_VALUE);
        }
    }
}
