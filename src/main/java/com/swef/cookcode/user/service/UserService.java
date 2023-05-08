package com.swef.cookcode.user.service;

import static com.swef.cookcode.common.ErrorCode.LOGIN_PARAM_REQUIRED;
import static com.swef.cookcode.common.ErrorCode.USER_ALREADY_EXISTS;
import static com.swef.cookcode.common.ErrorCode.USER_NOT_FOUND;
import static org.springframework.util.StringUtils.hasText;

import com.swef.cookcode.common.error.exception.AlreadyExistsException;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.user.domain.Authority;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.request.UserSignUpRequest;
import com.swef.cookcode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User signIn(String principal, String credentials) {
        if (!hasText(principal) || !hasText(credentials)) {
            throw new InvalidRequestException(LOGIN_PARAM_REQUIRED);
        }
        User user = userRepository.findByEmailAndIsQuit(principal, false)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        user.checkPassword(passwordEncoder, credentials);
        return user;
    }

    @Transactional
    public User signUp(UserSignUpRequest request) {
        if (userRepository.existsByEmailAndIsQuit(request.getEmail(), false)) {
            throw new AlreadyExistsException(USER_ALREADY_EXISTS);
        }

        //TODO: 이메일 인증 로직

        User.validatePassword(request.getPassword());

        User newUser = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .encodedPassword(passwordEncoder.encode(request.getPassword()))
                .authority(Authority.USER)
                .build();

        return userRepository.save(newUser);
    }

    @Transactional
    public User quit(User user) {
        user.quit();
        return user;
    }

}
