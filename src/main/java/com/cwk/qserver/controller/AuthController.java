package com.cwk.qserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cwk.qserver.constant.LoggingConstant;
import com.cwk.qserver.dao.IService.impl.UserServiceimpl;
import com.cwk.qserver.dao.Response;
import com.cwk.qserver.dao.entity.User;
import lombok.Data;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
            QueryWrapper<User> wrapper = Wrappers.query();
            wrapper.eq("username",loggingpara.username).eq("password",loggingpara.password);
            User rs = userServiceimpl.getOne(wrapper);
            if(rs==null){
                return Response.builder().code(LoggingConstant.USERNAME_OR_PASSWORD_ERR)
                        .msg("USERNAME OR PASSWORD ERR")
                        .data("").build();
            }else {
                return Response.builder().code(LoggingConstant.PASS).msg("Logging success").data(rs).build();
            }
        }
        catch (Exception e){
            return Response.builder().code(LoggingConstant.UNEXCEP_ERR).msg("err "+e.toString()).data("").build();
        }
    }

    @PostMapping("/sign")
    @ResponseBody
    public Response signing(@RequestBody User signing){
        try {
            QueryWrapper<User> ext=Wrappers.query();
            ext.eq("username",signing.username);
            User res = userServiceimpl.getOne(ext);
            if(res==null){//不存在
                userServiceimpl.save(signing);
                return Response.builder().code(LoggingConstant.SIGN_IN_SUCCESS).msg("Signed").data("").build();
            }else {
                log.info("signing with a already existed username");
                return  Response.builder().code(LoggingConstant.USER_ALREADY_EXIST)
                        .msg("USER_ALREADY_EXIST").data("").build();
            }
        }catch (Exception e){
            log.warn("Unexcepted err"+e.toString());
            return Response.builder().code(LoggingConstant.UNEXCEP_ERR).msg("Unexcept_err").data("").build();
        }
    }
}
