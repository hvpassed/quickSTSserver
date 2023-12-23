package com.cwk.qserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cwk.qserver.card.Card;
import com.cwk.qserver.card.CardsPile;
import com.cwk.qserver.card.factory.CardFactory;
import com.cwk.qserver.card.factory.CardsPileFactory;
import com.cwk.qserver.dao.IService.impl.UserServiceimpl;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.service.battle.BattleManager;
import com.cwk.qserver.service.security.RSAKeyGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Service
class QServerApplicationTests {
    @Value("${privateKey}")
    private String privateKey;

    @Value("${publicKey}")
    private String publicKey;

    @Test
    public void readKey(){
        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+publicKey);
    }
    @Test
    public void testRSA() throws NoSuchAlgorithmException {
        KeyPair keyPair = RSAKeyGenerator.generateKeyPair();


        // 将公钥转换为字符串
        Map<String,String> keys = convertKeyToString(keyPair);
        String privateKey = keys.get("privateKey");
        String publicKey = keys.get("publicKey");
        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+publicKey);
    }

    public Map<String,String> convertKeyToString(KeyPair key) {
        byte[] publicKeyBytes = key.getPublic().getEncoded();
        byte[] privateKeyBytes = key.getPrivate().getEncoded();
        Map<String,String> ret = new HashMap<>();
        ret.put("publicKey",Base64.getEncoder().encodeToString(publicKeyBytes));
        ret.put("privateKey",Base64.getEncoder().encodeToString(privateKeyBytes));

        return ret;
    }
}
