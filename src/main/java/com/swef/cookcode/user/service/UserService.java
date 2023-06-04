package com.swef.cookcode.user.service;

import static com.swef.cookcode.common.ErrorCode.LOGIN_PARAM_REQUIRED;
import static com.swef.cookcode.common.ErrorCode.USER_ALREADY_EXISTS;
import static com.swef.cookcode.common.ErrorCode.USER_NOT_FOUND;
import static org.springframework.util.StringUtils.hasText;

import com.swef.cookcode.common.dto.UrlResponse;
import com.swef.cookcode.common.error.exception.AlreadyExistsException;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.util.S3Util;
import com.swef.cookcode.user.domain.Authority;
import com.swef.cookcode.user.domain.Subscribe;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.request.UserSignUpRequest;
import com.swef.cookcode.user.dto.response.UserDetailResponse;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import com.swef.cookcode.user.repository.SubscribeRepository;
import com.swef.cookcode.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final SubscribeRepository subscribeRepository;

    private final S3Util s3Util;

    private final static String PROFILEIMAGE_DIRECTORY = "profileImage";
    @Transactional
    public UrlResponse updateProfileImage(User user, MultipartFile profileImage) {
        String newUrl = "";
        if (!profileImage.isEmpty()) {
            newUrl = s3Util.upload(profileImage, PROFILEIMAGE_DIRECTORY);
        }
        if (hasText(user.getProfileImage())) {
            s3Util.deleteFile(user.getProfileImage());
        }
        user.updateProfileImage(newUrl);
        userRepository.save(user);
        return UrlResponse.builder()
                .urls(List.of(newUrl))
                .build();
    }

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
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            User existUser = userRepository.findByEmail(request.getEmail()).get();
            if (existUser.getIsQuit()) {
                existUser.rejoin();
                return userRepository.save(existUser);
            }
            else {
                throw new AlreadyExistsException(USER_ALREADY_EXISTS);
            }
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
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Slice<UserDetailResponse> searchUsersWith(Long userId, String searchQuery, Pageable pageable) {
        return userRepository.findByNicknameContaining(userId, searchQuery, pageable);
    }

    @Transactional
    public void toggleSubscribe(User user, Long createrId) {
        User publisher = userRepository.getReferenceById(createrId);

        Optional<Subscribe> subscribeOptional = subscribeRepository.findBySubscriberAndPublisher(user, publisher);

        subscribeOptional.ifPresentOrElse(this::unSubscribe, () -> subscribe(user, publisher));
    }

    void subscribe(User user, User publisher) {
        Subscribe subscribe = Subscribe.createEntity(user, publisher);

        subscribeRepository.save(subscribe);
    }
    void unSubscribe(Subscribe subscribe) {
        subscribeRepository.delete(subscribe);
    }


    @Transactional(readOnly = true)
    public Slice<UserDetailResponse> getSubscribers(Pageable pageable, User user) {
//        List<Subscribe> subscribes = subscribeRepository.findSubscribers(user);
        return userRepository.findSubscribers(pageable, user.getId());
    }

    @Transactional(readOnly = true)
    public Slice<UserDetailResponse> getPublishers(Pageable pageable, User user) {
        return userRepository.findPublishers(pageable, user.getId());
    }

}
