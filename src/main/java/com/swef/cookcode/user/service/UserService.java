package com.swef.cookcode.user.service;

import static com.swef.cookcode.common.ErrorCode.LOGIN_PARAM_REQUIRED;
import static com.swef.cookcode.common.ErrorCode.SUBSCRIBE_MYSELF;
import static com.swef.cookcode.common.ErrorCode.USER_ALREADY_EXISTS;
import static com.swef.cookcode.common.ErrorCode.USER_NOT_FOUND;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.dto.UrlResponse;
import com.swef.cookcode.common.error.exception.AlreadyExistsException;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.error.exception.PermissionDeniedException;
import com.swef.cookcode.common.util.S3Util;
import com.swef.cookcode.user.domain.Authority;
import com.swef.cookcode.user.domain.Status;
import com.swef.cookcode.user.domain.Subscribe;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.request.ChangePasswordRequest;
import com.swef.cookcode.user.dto.request.UserSignUpRequest;
import com.swef.cookcode.user.dto.response.UserDetailResponse;
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

    private final UserSimpleService userSimpleService;


    private final static String PROFILEIMAGE_DIRECTORY = "profileImage";
    @Transactional
    public UrlResponse updateProfileImage(User user, MultipartFile profileImage) {
        String newUrl = "";
        if (nonNull(profileImage) && !profileImage.isEmpty()) {
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
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            User existUser = userOptional.get();
            if (existUser.getIsQuit()) {
                existUser.rejoin();
                return userRepository.save(existUser);
            }
            else {
                throw new AlreadyExistsException(USER_ALREADY_EXISTS);
            }
        }

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
        if (user.getId().equals(createrId)) throw new InvalidRequestException(SUBSCRIBE_MYSELF);

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
        return userRepository.findSubscribers(pageable, user.getId());
    }

    @Transactional(readOnly = true)
    public Slice<UserDetailResponse> getPublishers(Pageable pageable, User user) {
        return userRepository.findPublishers(pageable, user.getId());
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        user.checkPassword(passwordEncoder, request.getPassword());
        user.changePassword(passwordEncoder, request.getNewPassword());
        userRepository.save(user);
    }

    @Transactional
    public void changeToTemporaryPassword(String email, String temporaryPassword) {
        User user = userSimpleService.getUserByEmail(email);
        user.changePassword(passwordEncoder, temporaryPassword);
        userRepository.save(user);
    }

    @Transactional
    public void requestPermission(User user, Authority authority) {
        if (authority == Authority.INFLUENCER) {
            validateInitialConditionOfInfluencer(user.getId());
            user.changeStatus(Status.INF_REQUESTED);
        }
        if (authority == Authority.ADMIN) user.changeStatus(Status.ADM_REQUESTED);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void validateInitialConditionOfInfluencer(Long userId) {
        if(!userRepository.fulfillInfluencerCondition(userId)) throw new PermissionDeniedException(ErrorCode.INFLUENCER_FALL);
    }
}
