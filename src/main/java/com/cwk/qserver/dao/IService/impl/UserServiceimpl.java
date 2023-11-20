package com.cwk.qserver.dao.IService.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwk.qserver.dao.IService.UserService;
import com.cwk.qserver.dao.entity.User;
import com.cwk.qserver.dao.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class UserServiceimpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public boolean save(User entity) {
        QueryWrapper<User> wrapper = Wrappers.query();
        wrapper.orderByDesc("id").last("LIMIT 1");
        User res = this.getOne(wrapper);
        entity.id=res.id+1;
        log.info(String.format("Signed username:%s,id:%d",entity.username,entity.id));
        return super.save(entity);
    }
}
