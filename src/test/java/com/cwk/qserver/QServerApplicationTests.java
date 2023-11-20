package com.cwk.qserver;

import com.cwk.qserver.dao.IService.impl.UserServiceimpl;
import com.cwk.qserver.dao.entity.User;
import com.cwk.qserver.dao.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

@SpringBootTest
@Service
class QServerApplicationTests {
    @Autowired
    private UserServiceimpl userServiceimpl;

    @Test
    public void test(){
        User us = new User();
        us.setPassword("998");
        us.setUsername("65498");
        userServiceimpl.save(us);
    }
}
