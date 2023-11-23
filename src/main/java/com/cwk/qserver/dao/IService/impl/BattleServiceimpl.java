package com.cwk.qserver.dao.IService.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwk.qserver.dao.IService.BattleService;
import com.cwk.qserver.dao.entity.Battle;
import com.cwk.qserver.dao.mapper.BattleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BattleServiceimpl extends ServiceImpl<BattleMapper, Battle> implements BattleService {

}
