package com.netcracker.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class SecurityUtil {
    static Random random = new Random();
    public static String encryptPassword(String password) {
        Base64.Encoder enc = Base64.getEncoder();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory f = null;
        try {
            f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            return enc.encodeToString(salt);
        }
        try {
            byte[] hash = f.generateSecret(spec).getEncoded();
            return enc.encodeToString(hash);
        } catch (InvalidKeySpecException e) {
            return enc.encodeToString(salt);
        }
    }
}
