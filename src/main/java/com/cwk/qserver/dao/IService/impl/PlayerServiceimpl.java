package com.cwk.qserver.dao.IService.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwk.qserver.dao.IService.PlayerService;
import com.cwk.qserver.dao.entity.Map;
import com.cwk.qserver.dao.entity.Player;
import com.cwk.qserver.dao.mapper.PlayerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class PlayerServiceimpl extends ServiceImpl<PlayerMapper, Player> implements PlayerService {


}
