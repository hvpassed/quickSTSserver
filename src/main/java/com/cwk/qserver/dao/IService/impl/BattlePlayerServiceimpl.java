package com.cwk.qserver.dao.IService.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwk.qserver.dao.IService.BattlePlayerService;
import com.cwk.qserver.dao.mapper.BattlePlayerMapper;
import com.cwk.qserver.target.BattlePlayer;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.dao.IService.impl
 * @Author: chen wenke
 * @CreateTime: 2023-11-29 23:46
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class BattlePlayerServiceimpl extends ServiceImpl<BattlePlayerMapper, BattlePlayer> implements BattlePlayerService {
}
