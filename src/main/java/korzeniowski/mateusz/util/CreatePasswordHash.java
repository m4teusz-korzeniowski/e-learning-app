package korzeniowski.mateusz.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class CreatePasswordHash {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();


    public static String createHashBCrypt(String password) {
        return "{bcrypt}" + ENCODER.encode(password);
    }

    public static void main(String[] args) {
        System.out.println(createHashBCrypt("pass1"));
        System.out.println(createHashBCrypt("pass2"));
        System.out.println(createHashBCrypt("pass3"));
        System.out.println(createHashBCrypt("pass4"));
    }
}
