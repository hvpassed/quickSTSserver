package com.cwk.qserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cwk.qserver.constant.LoggingConstant;
import com.cwk.qserver.dao.IService.impl.UserServiceimpl;
import com.cwk.qserver.dao.Response;
import com.cwk.qserver.dao.entity.User;
import com.cwk.qserver.service.security.RSAKeyGenerator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
@Data
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserServiceimpl userServiceimpl;



    @PostMapping("/logging")
    @ResponseBody
    public Response logging(@RequestBody User loggingpara){
        try {
            log.info("try log in user:"+loggingpara.getUsername());
            QueryWrapper<User> wrapper = Wrappers.query();


            wrapper.eq("username",loggingpara.username);
            User rs = userServiceimpl.getOne(wrapper);
            if(rs==null){
                return Response.builder().code(LoggingConstant.USERNAME_OR_PASSWORD_ERR)
                        .msg("USERNAME OR PASSWORD ERR")
                        .data("").build();
            }

            String hashedPassword = rs.getPassword();
            String decodePassword = decryptPassword(loggingpara.getPassword(),rs.getPrivateKey());
            boolean isCorrect = BCrypt.checkpw(decodePassword, hashedPassword);
            if(isCorrect){
                rs.setPassword("");
                rs.setPublicKey("");
                rs.setPrivateKey("");
                return Response.builder().code(LoggingConstant.PASS).msg("Logging Success").data(rs).build();
            }
            else {
                return Response.builder().code(LoggingConstant.USERNAME_OR_PASSWORD_ERR)
                        .msg("USERNAME OR PASSWORD ERR")
                        .data("").build();
            }
        }
        catch (Exception e){
            log.error(e.toString());
            return Response.builder().code(LoggingConstant.UNEXCEP_ERR).msg("err "+e.toString()).data("").build();
        }
    }

    @PostMapping("/getPublicKey")
    @ResponseBody
    public Response getPublicKey(@RequestBody User entity){
        try {

            QueryWrapper<User> wrapper = Wrappers.query();


            wrapper.eq("username",entity.username);
            User rs = userServiceimpl.getOne(wrapper);
            if(rs==null){
                return Response.builder().code(LoggingConstant.USERNAME_OR_PASSWORD_ERR)
                        .msg("USERNAME OR PASSWORD ERR")
                        .data("").build();
            }
            String publicKey = rs.getPublicKey();
            return Response.builder().code(LoggingConstant.PASS).msg("Get Key Success").data(publicKey).build();

        }
        catch (Exception e){
            log.error(e.toString());
            return Response.builder().code(LoggingConstant.UNEXCEP_ERR).msg("err "+e.toString()).data("").build();
        }
    }

    @PostMapping("/signEnsureUserName")
    @ResponseBody
    public Response signEnsureUserName(@RequestBody User signing){
        try {
            QueryWrapper<User> ext=Wrappers.query();
            ext.eq("username",signing.username);
            User res = userServiceimpl.getOne(ext);
            if(res!=null) {
                log.info("signing with a already existed username");
                return  Response.builder().code(LoggingConstant.USER_ALREADY_EXIST)
                        .msg("USER_ALREADY_EXIST").data("").build();
            }
            signing.setHasmap(0);
            KeyPair keyPair = RSAKeyGenerator.generateKeyPair();


            // 将公钥转换为字符串
            Map<String,String> keys = convertKeyToString(keyPair);
            String privateKey = keys.get("privateKey");
            String publicKey = keys.get("publicKey");

            signing.setPublicKey(publicKey);
            signing.setPrivateKey(privateKey);
            userServiceimpl.save(signing);
            return Response.builder().code(LoggingConstant.SIGN_IN_SUCCESS).msg("SignForUserName").data(publicKey).build();
        }catch (Exception e){
            log.error("Unexcepted err"+e.toString());
            return Response.builder().code(LoggingConstant.UNEXCEP_ERR).msg("Unexcept_err").data("").build();
        }
    }

    @PostMapping("/sign")
    @ResponseBody
    public Response sign(@RequestBody User entity){
        try {
            String username =entity.getUsername();
            String password = entity.getPassword();//加密过的
            QueryWrapper<User> userQueryWrapper = Wrappers.query();
            userQueryWrapper.eq("username",username);
            User user = userServiceimpl.getOne(userQueryWrapper);
            if(user==null){
                return Response.builder().code(LoggingConstant.UNEXCEP_ERR)
                        .msg("USERNAME OR PASSWORD ERR")
                        .data("").build();
            }
            String decodePassword = decryptPassword(password,user.getPrivateKey());

            String hashedPassword = BCrypt.hashpw(decodePassword, BCrypt.gensalt());
            user.setPassword(hashedPassword);
            userServiceimpl.saveOrUpdate(user);
            return Response.builder().code(LoggingConstant.SIGN_IN_SUCCESS).msg("signed").data("").build();
        }catch (Exception e){
            e.printStackTrace();
            log.error("Unexcepted err"+e.toString());
            return Response.builder().code(LoggingConstant.UNEXCEP_ERR).msg("Unexcept_err").data("").build();

        }
    }


    public Map<String,String> convertKeyToString(KeyPair key) {
        byte[] publicKeyBytes = key.getPublic().getEncoded();
        byte[] privateKeyBytes = key.getPrivate().getEncoded();
        Map<String,String> ret = new HashMap<>();
        ret.put("publicKey",Base64.getEncoder().encodeToString(publicKeyBytes));
        ret.put("privateKey",Base64.getEncoder().encodeToString(privateKeyBytes));

        return ret;
    }
    public static String decryptPassword(String encryptedPassword, String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedPassword);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);

    }
}
