package com.swef.cookcode.common.util;

import com.swef.cookcode.user.domain.User;
import java.security.SecureRandom;
import java.util.Random;

public class PasswordUtil {

    private final static String lowLetters = "abcdefghijklmnopqrstuvwxyz";
    private final static String capitalLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String numbers = "0123456789";
    private final static String specialCharacters = "!@#$%^&*()";
    private final static String allCharacters = lowLetters + capitalLetters + numbers + specialCharacters;
    private final static Random random = new SecureRandom();

    public static String generateTemporaryPassword(int size) {
        StringBuilder key = new StringBuilder();
        appendRandomSingleCharacterFrom(lowLetters, key);
        appendRandomSingleCharacterFrom(capitalLetters, key);
        appendRandomSingleCharacterFrom(numbers, key);
        appendRandomSingleCharacterFrom(specialCharacters, key);

        for (int i = 4; i < size; i++) {
           appendRandomSingleCharacterFrom(allCharacters, key);
        }

        User.validatePassword(key.toString());
        return key.toString();
    }

    public static String createNumberCode(int size) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < size; i++) {
            key.append(random.nextInt(10));
        }
        return key.toString();
    }

    private static void appendRandomSingleCharacterFrom(String candidateLetters, StringBuilder key) {
        key.append(candidateLetters.charAt(random.nextInt(candidateLetters.length())));
    }
}
