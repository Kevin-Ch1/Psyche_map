package com.psychemap.security.crypto;

public interface PasswordHasher {

    String hash(String plainPassword);

    boolean verify(String plainPassword, String hashedPassword);
}
