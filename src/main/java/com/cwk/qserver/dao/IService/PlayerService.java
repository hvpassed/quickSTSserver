package com.cwk.qserver.dao.IService;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cwk.qserver.dao.entity.Player;

public interface PlayerService extends IService<Player> {
    @Override
    default boolean saveOrUpdate(Player entity, Wrapper<Player> updateWrapper) {


        return IService.super.saveOrUpdate(entity, updateWrapper);
    }
}
