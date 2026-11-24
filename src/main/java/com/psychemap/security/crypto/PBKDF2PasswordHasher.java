package com.psychemap.security.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PBKDF2PasswordHasher implements PasswordHasher {

    private final int iteraciones;
    private static final int LONGITUD_HASH_BITS = 256;
    private static final int LONGITUD_SAL_BYTES = 16;
    private static final String ALGORITMO = "PBKDF2WithHmacSHA512";

    public PBKDF2PasswordHasher() {
        this(10000);
    }

    public PBKDF2PasswordHasher(int iteraciones) {
        this.iteraciones = (iteraciones > 0) ? iteraciones : 10000;
    }

    @Override
    public String hash(String contraseniaPlana) {
        if (contraseniaPlana == null || contraseniaPlana.isEmpty()) {
            throw new IllegalArgumentException("La contraseña a hashear no puede estar vacía.");
        }

        byte[] sal = new byte[LONGITUD_SAL_BYTES];
        new SecureRandom().nextBytes(sal);

        byte[] hash = calcularHash(contraseniaPlana.toCharArray(), sal);

        String salBase64 = Base64.getEncoder().encodeToString(sal);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);
        return String.format("$pbkdf2-sha512$%d$%s$%s", this.iteraciones, salBase64, hashBase64);
    }

    @Override
    public boolean verify(String contraseniaPlana, String hashAlmacenado) {
        if (contraseniaPlana == null || hashAlmacenado == null || hashAlmacenado.isEmpty()) {
            return false;
        }

        try {
            byte[] sal;
            byte[] hashEsperado;
            int iter = this.iteraciones;

            if (hashAlmacenado.startsWith("$pbkdf2-sha512$")) {
                String[] partes = hashAlmacenado.split("\\$");
                iter = Integer.parseInt(partes[2]);
                sal = Base64.getDecoder().decode(partes[3]);
                hashEsperado = Base64.getDecoder().decode(partes[4]);
            } else if (hashAlmacenado.contains(":")) {
                String[] partes = hashAlmacenado.split(":");
                sal = Base64.getDecoder().decode(partes[0]);
                hashEsperado = Base64.getDecoder().decode(partes[1]);
            } else {
                return false;
            }

            byte[] hashCalculado = calcularHash(contraseniaPlana.toCharArray(), sal, iter);

            return MessageDigest.isEqual(hashCalculado, hashEsperado);
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] calcularHash(char[] password, byte[] sal) {
        return calcularHash(password, sal, this.iteraciones);
    }

    private byte[] calcularHash(char[] password, byte[] sal, int iter) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, sal, iter, LONGITUD_HASH_BITS);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITMO);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al calcular hash de contraseña con " + ALGORITMO, e);
        }
    }
}
