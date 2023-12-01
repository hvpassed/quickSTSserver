package com.cwk.qserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cwk.qserver.card.Card;
import com.cwk.qserver.card.factory.CardFactory;
import com.cwk.qserver.constant.ResponseConstant;
import com.cwk.qserver.dao.IService.impl.PlayerServiceimpl;
import com.cwk.qserver.dao.Response;
import com.cwk.qserver.dao.entity.Player;
import com.cwk.qserver.dao.entity.User;
import com.cwk.qserver.service.battle.BattleManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.controller
 * @Author: chen wenke
 * @CreateTime: 2023-11-30 09:49
 * @Description: TODO
 * @Version: 1.0
 */
@Controller
@Slf4j
@Data
@RequestMapping("/battle")
public class BattleController {
    @Autowired
    private PlayerServiceimpl playerService;

    @Autowired
    private CardFactory cardFactory;
    @PostMapping("/initBattle")
    @ResponseBody
    public Response initBattle(@RequestBody User entity){
        try {
            QueryWrapper<Player> playerQueryWrapper = Wrappers.query();
            playerQueryWrapper.eq("userid",entity.getUserid()).eq("mapid",entity.getMapid());
            Player player = playerService.getOne(playerQueryWrapper);
            if(player == null){
                throw new Exception();
            }

            Map<String,Object> ret = BattleManager.BattleManagerInitAndSava(entity.getUserid(),entity.getMapid(),entity.getEnterPos());
            player.setPlaying(1);
            player.setPlaypos(entity.getEnterPos());
            playerService.saveOrUpdate(player,playerQueryWrapper);
            return Response.builder().code(ResponseConstant.RES_OK).msg("初始化成功").data(ret).build();
        }catch (Exception e)
        {
            e.printStackTrace();
            return  Response.builder().code(ResponseConstant.RES_ILLEGAL_ACTION).msg("未知错误:" + e.getMessage()).data("").build();
        }

    }



    @PostMapping("/getCardsByIdMap")
    @ResponseBody
    public Response getCardsById(@RequestBody Params entity){
        try {
            Set<Integer> cardIds = entity.cardIdSetMap();
            List<Card> objects = cardFactory.createCardById(cardIds);
            return Response.builder().code(ResponseConstant.RES_OK).msg("成功构造卡牌对象").data(objects).build();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Response.builder().code(ResponseConstant.RES_NEED_CHECK).msg("未知错误："+e.getMessage()).data("").build();
        }

    }

}

class  Params{
    @JsonProperty("cardids")
    private String cardids;
    @JsonProperty("cardidsmap")
    private String cardidsmap;

    public Set<Integer> cardIdSetMap(){

        JSONObject jsonObject = JSON.parseObject(cardidsmap);

        Set array = Set.copyOf(jsonObject.getObject("Array", List.class));

        return array;

    }

    public Set<Integer> cardIdSet(){
        String json = String.format("{\"Array\":%s}",this.cardids);
        JSONObject jsonObject = JSON.parseObject(json);

        Set array = Set.copyOf(jsonObject.getObject("Array", List.class));

        return array;

    }
}
