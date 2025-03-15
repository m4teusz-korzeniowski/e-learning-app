package korzeniowski.mateusz.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CreatePasswordHash {
    public static String createHashBCrypt(String password) {
        return "{bcrypt}" + new BCryptPasswordEncoder().encode(password);
    }

    public static void main(String[] args) {
        System.out.println(createHashBCrypt("pass1"));
        System.out.println(createHashBCrypt("pass2"));
        System.out.println(createHashBCrypt("pass3"));
        System.out.println(createHashBCrypt("pass4"));
    }
}
