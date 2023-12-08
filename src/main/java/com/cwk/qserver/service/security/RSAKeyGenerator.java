package com.cwk.qserver.service.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.service.security
 * @Author: chen wenke
 * @CreateTime: 2023-12-08 18:15
 * @Description: TODO
 * @Version: 1.0
 */

public class RSAKeyGenerator {
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // 2048位的密钥长度，可以根据需要调整
        return keyPairGenerator.generateKeyPair();
    }
}
