package com.cwk.qserver.dao.IService.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwk.qserver.dao.IService.MonsterService;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.dao.mapper.MonsterMapper;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.dao.IService.impl
 * @Author: chen wenke
 * @CreateTime: 2023-11-29 23:37
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class MonsterServiceimpl extends ServiceImpl<MonsterMapper, Monster> implements MonsterService {
}
