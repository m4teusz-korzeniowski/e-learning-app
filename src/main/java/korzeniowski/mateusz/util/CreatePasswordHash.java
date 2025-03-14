package korzeniowski.mateusz.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CreatePasswordHash {
    public String createHashBCrypt(String password) {
        return "{bcrypt}" + new BCryptPasswordEncoder().encode(password);
    }

    public static void main(String[] args) {
        CreatePasswordHash createPasswordHash = new CreatePasswordHash();
        System.out.println(createPasswordHash.createHashBCrypt("pass1"));
        System.out.println(createPasswordHash.createHashBCrypt("pass2"));
        System.out.println(createPasswordHash.createHashBCrypt("pass3"));
        System.out.println(createPasswordHash.createHashBCrypt("pass4"));
    }
}
