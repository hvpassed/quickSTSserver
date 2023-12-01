package com.cwk.qserver.dao.IService.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwk.qserver.dao.IService.IntentService;
import com.cwk.qserver.dao.Intent;
import com.cwk.qserver.dao.mapper.IntentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.dao.IService.impl
 * @Author: chen wenke
 * @CreateTime: 2023-12-01 18:50
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Service
public class IntentServiceimpl extends ServiceImpl<IntentMapper, Intent> implements IntentService {



}
