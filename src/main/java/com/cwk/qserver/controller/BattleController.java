package com.cwk.qserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cwk.qserver.card.Card;
import com.cwk.qserver.card.factory.CardFactory;
import com.cwk.qserver.constant.CardTargetConstant;
import com.cwk.qserver.constant.ResponseConstant;
import com.cwk.qserver.dao.IService.impl.BattlePlayerServiceimpl;
import com.cwk.qserver.dao.IService.impl.PlayerServiceimpl;
import com.cwk.qserver.dao.Response;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.dao.entity.Player;
import com.cwk.qserver.dao.entity.User;
import com.cwk.qserver.service.battle.BattleManager;
import com.cwk.qserver.target.BattlePlayer;
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

    @Autowired
    private BattlePlayerServiceimpl battlePlayerService;
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

    @PostMapping("/playCards")
    @ResponseBody
    public Response playCards(@RequestBody ParamsCard entity){
        try {
            System.out.println(entity);
            Map<String,Object> ret = entity.getCardPileAfter();
            int cardid = (int) ret.get("cardid");
            BattleManager battleManager = new BattleManager(entity.getUserid());
            Set<Integer> cardids = new HashSet<>();
            cardids.add(cardid);
            Card card = cardFactory.createCardById(cardids).get(0);
            if(card==null){
                throw new RuntimeException();
            }
            if(card.getSelect()== CardTargetConstant.SINGLE_MONSTER){
               Monster monster = battleManager.CardImpactMonster(cardid,entity.getTargetMonsterid());
               ret.put("monster",monster);
            } else if (card.getSelect()==CardTargetConstant.ALL_MONSTERS){
                List<Monster> monsters = battleManager.CardImpactAllMonster(cardid);
                ret.put("monsters",monsters);
            }else if(card.getSelect()==CardTargetConstant.SELF){
                BattlePlayer battlePlayerIn = battleManager.CardImpactPlayer(cardid);

                battlePlayerIn.setDrawPile((List<Integer>) ret.get("drawCardids"));
                battlePlayerIn.setHandPile((List<Integer>) ret.get("handCardids"));
                battlePlayerIn.setDiscordPile((List<Integer>) ret.get("discordCardids"));
                battlePlayerIn.serialize();
                ret.put("battle_player",battlePlayerIn);
            }
            ret.put("target_type",card.getSelect());
            BattlePlayer battlePlayer = battlePlayerService.getById(entity.getUserid());
            battlePlayer.setDrawPile((List<Integer>) ret.get("drawCardids"));
            battlePlayer.setHandPile((List<Integer>) ret.get("handCardids"));
            battlePlayer.setDiscordPile((List<Integer>) ret.get("discordCardids"));
            battlePlayer.serialize();
            battlePlayerService.saveOrUpdate(battlePlayer);

            return Response.builder().code(ResponseConstant.RES_OK).msg("成功出牌").data(ret).build();

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
@Data
@Slf4j
class ParamsCard{
    @JsonProperty("userid")
    public int userid;
    @JsonProperty("target_monsterid")
    public int targetMonsterid;
    @JsonProperty("index")
    public int index;
    @JsonProperty("hand_card_ids")
    public String handCardIds;
    //["{id,cardid,width,cardName,cardCost,cardDc}"]
    @JsonProperty("draw_card_ids")
    public String drawCardIds;


    @JsonProperty("discord_card_ids")
    public String discordCardIds;

    @JsonProperty("hand_ids")
    public String handIds;

    @JsonProperty("draw_ids")
    public String drawIds;
    @JsonProperty("discord_ids")
    public String discordIds;

    public Map<String,Object> getCardPileAfter() throws RuntimeException{
        //获取出牌后的牌组，以及cardid
        List<Integer> handCardIdsList = handCardIdsList();
        List<Integer> drawCardIdsList = drawCardIdsList();
        List<Integer> discordCardIdsList = discordCardIdsList();
        int cardid = -1;
        List<Integer> handIdsList = handIdsList();
        List<Integer> drawIdsList = drawIdsList();
        List<Integer> discordIdsList = discordIdsList();
        boolean flag= true;
        for(int i = 0;i<handIdsList.size();i++){
            if(handIdsList.get(i)==index){
                handIdsList.remove(i);
                cardid =handCardIdsList.remove(i);
                discordCardIdsList.add(cardid);
                discordIdsList.add(index);
                flag=false;
                break;
            }
        }
        if(flag){
            log.error("Can not find cardid by index:"+index+" in handIdsList");
            throw new RuntimeException();
        }

        Map<String,Object> ret = new HashMap<>();

        ret.put("handCardids",handCardIdsList);
        ret.put("discordCardids",discordCardIdsList);
        ret.put("drawCardids",drawCardIdsList);
        ret.put("cardid", cardid);
        if(cardid ==-1){
            log.error("Can not find cardid by index:"+index+" in handIdsList");
            throw new RuntimeException();
        }
        return ret;
    }

    public List<Integer> handCardIdsList(){
        String json = String.format("{\"Array\":%s}",this.handCardIds);
        JSONObject jsonObject = JSON.parseObject(json);

        List array = jsonObject.getObject("Array", List.class);

        return array;
    }

    public List<Integer> drawCardIdsList(){
        String json = String.format("{\"Array\":%s}",this.drawCardIds);
        JSONObject jsonObject = JSON.parseObject(json);

        List array = jsonObject.getObject("Array", List.class);

        return array;
    }

    public List<Integer> discordCardIdsList(){
        String json = String.format("{\"Array\":%s}",this.discordCardIds);
        JSONObject jsonObject = JSON.parseObject(json);

        List array = jsonObject.getObject("Array", List.class);

        return array;
    }

    public List<Integer> handIdsList(){
        String json = String.format("{\"Array\":%s}",this.handIds);
        JSONObject jsonObject = JSON.parseObject(json);

        List array =jsonObject.getObject("Array", List.class);

        return array;
    }

    public List<Integer> drawIdsList(){
        String json = String.format("{\"Array\":%s}",this.drawIds);
        JSONObject jsonObject = JSON.parseObject(json);

        List array = jsonObject.getObject("Array", List.class);

        return array;
    }

    public List<Integer> discordIdsList(){
        String json = String.format("{\"Array\":%s}",this.discordIds);
        JSONObject jsonObject = JSON.parseObject(json);

        List array = jsonObject.getObject("Array", List.class);

        return array;
    }
}
