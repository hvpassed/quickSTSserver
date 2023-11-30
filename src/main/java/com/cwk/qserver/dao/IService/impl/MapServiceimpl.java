package com.cwk.qserver.dao.IService.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwk.qserver.dao.IService.MapService;
import com.cwk.qserver.dao.entity.MapEntity;
import com.cwk.qserver.dao.mapper.MapMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class MapServiceimpl extends ServiceImpl<MapMapper, MapEntity> implements MapService {

    @Override
    public boolean saveOrUpdate(MapEntity entity, Wrapper<MapEntity> updateWrapper) {
        QueryWrapper<MapEntity> wrapper = Wrappers.query();
        wrapper.orderByDesc("mapid").last("LIMIT 1");
        MapEntity res = this.getOne(wrapper);
        if(res==null){
            entity.setMapid(0);
        }else {
            entity.setMapid(res.getMapid()+1);
        }
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        entity.setSeed(random.nextInt());
        entity.setCurrentposition("[-1,0]");
        return super.saveOrUpdate(entity, updateWrapper);
    }



}
