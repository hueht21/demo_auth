package com.cudev.demo_auth.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {


    public static String getPrivateKey() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("private.key");
        byte[] publicKeyBytes = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
        return new String(publicKeyBytes);
    }

    public static PrivateKey decodePrivateKey(String keyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        return key;
    }

    public static PublicKey decodePublicKey(String keyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(spec);
        return key;
    }

    public static String getPublicKey() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("public.key");
        byte[] publicKeyBytes = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
        return new String(publicKeyBytes);
    }
}
