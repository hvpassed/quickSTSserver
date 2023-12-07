package com.cwk.qserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cwk.qserver.bean.postBody.DeleteCardParam;
import com.cwk.qserver.bean.postBody.RecoverParam;
import com.cwk.qserver.card.Card;
import com.cwk.qserver.card.CardsPile;
import com.cwk.qserver.card.factory.CardFactory;
import com.cwk.qserver.constant.CardTargetConstant;
import com.cwk.qserver.constant.ResponseConstant;
import com.cwk.qserver.dao.IService.impl.*;
import com.cwk.qserver.dao.Response;
import com.cwk.qserver.dao.entity.*;
import com.cwk.qserver.service.battle.BattleManager;
import com.cwk.qserver.target.BattlePlayer;
import com.cwk.qserver.utils.ApplicationContextUtil;
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

    @Autowired
    private IntentServiceimpl intentService;
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

    @PostMapping("/initBattleBoss")
    @ResponseBody
    public Response initBattleBoss(@RequestBody User entity){
        try {
            QueryWrapper<Player> playerQueryWrapper = Wrappers.query();
            playerQueryWrapper.eq("userid",entity.getUserid()).eq("mapid",entity.getMapid());
            Player player = playerService.getOne(playerQueryWrapper);
            if(player == null){
                throw new Exception();
            }

            Map<String,Object> ret = BattleManager.BattleManagerInitAndSavaBoss(entity.getUserid(),entity.getMapid(),entity.getEnterPos());
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

    @PostMapping("/endTurn")
    @ResponseBody
    public Response endTurn(@RequestBody BattlePlayer entity){
        try {
            int userid = entity.getUserid();
            BattlePlayer battlePlayer = battlePlayerService.getById(userid);

            if(battlePlayer==null){
                log.error("Could not find battle_player by userid:"+userid);
                throw new RuntimeException();
            }
            battlePlayer.unSerialize();
            Random random = new Random(battlePlayer.getSeed());
            Map<String,Object> ret = CardsPile.drawCards(battlePlayer.getDrawPile(),battlePlayer.getDiscordPile(),battlePlayer.getHandPile(),battlePlayer.getDrawAmount(),random);
            battlePlayer.setSeed(random.nextInt());

            battlePlayer.setDrawPile((List<Integer>) ret.get("drawPile"));
            battlePlayer.setHandPile((List<Integer>) ret.get("handPile"));
            battlePlayer.setDiscordPile((List<Integer>) ret.get("discordPile"));
            battlePlayer.serialize();
            battlePlayerService.saveOrUpdate(battlePlayer);
            BattleManager battleManager = new BattleManager(userid);
            ret.putAll(battleManager.endTurn(userid));
            return Response.builder().code(ResponseConstant.RES_OK).msg("成功抽牌").data(ret).build();

        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Response.builder().code(ResponseConstant.RES_NEED_CHECK).msg("未知错误："+e.getMessage()).data("").build();
        }


    }

    @PostMapping("/reInitBattle")
    @ResponseBody
    public Response reInitBattle(@RequestBody User entity){
        try {
            return Response.builder().code(ResponseConstant.RES_OK).msg("成功重置").data( BattleManager.reInitBattle(entity.getUserid())).build();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Response.builder().code(ResponseConstant.RES_NEED_CHECK).msg("未知错误："+e.getMessage()).data("").build();
        }
    }

    @PostMapping("/existBattle")
    @ResponseBody
    public Response existBattle(@RequestBody Battle entity){
        try {
            BattleServiceimpl battleService = ApplicationContextUtil.getBean(BattleServiceimpl.class);
            QueryWrapper<Battle> queryWrapper = Wrappers.query();
            queryWrapper.eq("userid",entity.getUserid());
            Battle battle = battleService.getOne(queryWrapper);
            Map<String,Object> ret = new HashMap<>();

            if(battle==null){
                ret.put("existBattle",0);
                MapServiceimpl mapServiceimpl = ApplicationContextUtil.getBean(MapServiceimpl.class);
                QueryWrapper<MapEntity> mapEntityQueryWrapper = Wrappers.query();
                mapEntityQueryWrapper.eq("userid",entity.getUserid());
                MapEntity mapEntity = mapServiceimpl.getOne(mapEntityQueryWrapper);
                if(mapEntity==null){
                    log.error("Could not find mapEntity by userid:"+entity.getUserid());
                    throw new RuntimeException();
                }
                ret.put("seed",mapEntity.getSeed());

                QueryWrapper<Player> playerQueryWrapper = Wrappers.query();
                playerQueryWrapper.eq("userid",entity.getUserid());
                Player player = playerService.getOne(playerQueryWrapper);
                if(player==null){
                    log.error("Could not find player by userid:"+entity.getUserid());
                    throw new RuntimeException();
                }
                ret.put("curpos",player.getPlaypos());
                return Response.builder().code(ResponseConstant.RES_OK).msg("不存在战斗").data(ret).build();
            }else{
                ret.put("existBattle",1);
                return Response.builder().code(ResponseConstant.RES_OK).msg("存在战斗").data(ret).build();
            }

        }catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Response.builder().code(ResponseConstant.RES_NEED_CHECK).msg("未知错误：" + e.getMessage()).data("").build();
        }

    }


    @PostMapping("/winBattle")
    @ResponseBody
    public Response winBattle(@RequestBody User entity){
        try {
            BattleManager battleManager = new BattleManager(entity.getUserid());
            if(battleManager==null){
                log.error("Could not find battleManager by userid:"+entity.getUserid());
                throw new RuntimeException();
            }

            Map<String,Object> cards =  battleManager.winBattle(entity.getUserid());

            return Response.builder().code(ResponseConstant.RES_OK).msg("成功结束游戏").data(cards).build();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Response.builder().code(ResponseConstant.RES_NEED_CHECK).msg("未知错误："+e.getMessage()).data("").build();
        }
    }
    @PostMapping("/addCard")
    @ResponseBody
    public Response addCard(@RequestBody Player entity){
        try {
            int userid = entity.getUserid();
            int cardid = Integer.parseInt(entity.getCardids());
            int mapid = entity.getMapid();
            QueryWrapper<Player> queryWrapper = Wrappers.query();
            queryWrapper.eq("userid",userid).eq("mapid",mapid);
            Player player = playerService.getOne(queryWrapper);
            List<Integer> cardidsList = CardsPile.unSerialize(player.getCardids());
            cardidsList.add(cardid);
            player.setCardids(CardsPile.serialize(cardidsList));

            playerService.saveOrUpdate(player,queryWrapper);
            return Response.builder().code(ResponseConstant.RES_OK).msg("成功添加卡牌").data("").build();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Response.builder().code(ResponseConstant.RES_NEED_CHECK).msg("未知错误："+e.getMessage()).data("").build();
        }
    }

    @PostMapping("/deleteCard")
    @ResponseBody
    public Response deleteCard(@RequestBody DeleteCardParam entity) {
        try {
            int userid = entity.getUserid();
            int cardid = entity.getCardid();
            int mapid = entity.getMapid();

            log.info(String.format("userid:%d,mapid:%d,cardid:%d",userid,mapid,cardid));
            QueryWrapper<Player> queryWrapper = Wrappers.query();
            queryWrapper.eq("userid",userid).eq("mapid",mapid);
            Player player = playerService.getOne(queryWrapper);
            List<Integer> cardidsList = CardsPile.unSerialize(player.getCardids());
            cardidsList.remove(cardidsList.indexOf(cardid));
            player.setCardids(CardsPile.serialize(cardidsList));

            playerService.saveOrUpdate(player,queryWrapper);
            return Response.builder().code(ResponseConstant.RES_OK).msg("成功删除卡牌").data("").build();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Response.builder().code(ResponseConstant.RES_NEED_CHECK).msg("未知错误："+e.getMessage()).data("").build();
        }
    }

    @PostMapping("/recoverHp")
    @ResponseBody
    public Response recoverHp(@RequestBody RecoverParam entity){
        try {
            int userid = entity.getUserid();
            int mapid = entity.getMapid();
            int recoverHp = entity.getValue();
            log.info(String.format("userid:%d,mapid:%d,recoverHp:%d",userid,mapid,recoverHp));
            QueryWrapper<Player> queryWrapper = Wrappers.query();
            queryWrapper.eq("userid",userid).eq("mapid",mapid);
            Player player = playerService.getOne(queryWrapper);
            int nowhp = Math.min(player.getMaxhp(),player.getNowhp()+recoverHp);
            player.setNowhp(nowhp);
            playerService.saveOrUpdate(player,queryWrapper);
            return Response.builder().code(ResponseConstant.RES_OK).msg("成功恢复血量").data("").build();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Response.builder().code(ResponseConstant.RES_NEED_CHECK).msg("未知错误："+e.getMessage()).data("").build();
        }
    }

    @PostMapping("/totalWin")
    @ResponseBody
    public Response totalWin(@RequestBody Player entity){
        try {
            int userid = entity.getUserid();
            int mapid = entity.getMapid();

            //删除map
            QueryWrapper<MapEntity> mapEntityQueryWrapper = Wrappers.query();
            mapEntityQueryWrapper.eq("userid",userid).eq("mapid",mapid);
            MapServiceimpl mapServiceimpl = ApplicationContextUtil.getBean(MapServiceimpl.class);
            mapServiceimpl.remove(mapEntityQueryWrapper);

            //更新user
            QueryWrapper<User> userQueryWrapper = Wrappers.query();
            userQueryWrapper.eq("userid",userid);
            UserServiceimpl userServiceimpl = ApplicationContextUtil.getBean(UserServiceimpl.class);
            User user = userServiceimpl.getOne(userQueryWrapper);
            if(user==null){
                log.error("Could not find user by userid:"+userid);
                throw new RuntimeException();
            }
            user.setMapid(-1);
            user.setHasmap(0);
            userServiceimpl.saveOrUpdate(user,userQueryWrapper);

            return Response.builder().code(ResponseConstant.RES_OK).msg("成功删除游戏").data("").build();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Response.builder().code(ResponseConstant.RES_NEED_CHECK).msg("未知错误："+e.getMessage()).data("").build();
        }

    }

    @PostMapping("/lostGame")
    @ResponseBody
    public Response lostGame(@RequestBody Player entity){
        try {
            int userid = entity.getUserid();
            int mapid = entity.getMapid();
            BattleManager battleManager = new BattleManager(userid);
            battleManager.removeBattle(userid);//删除怪物以及战斗
            //删除map
            QueryWrapper<MapEntity> mapEntityQueryWrapper = Wrappers.query();
            mapEntityQueryWrapper.eq("userid",userid).eq("mapid",mapid);
            MapServiceimpl mapServiceimpl = ApplicationContextUtil.getBean(MapServiceimpl.class);
            mapServiceimpl.remove(mapEntityQueryWrapper);

            //更新user
            QueryWrapper<User> userQueryWrapper = Wrappers.query();
            userQueryWrapper.eq("userid",userid);
            UserServiceimpl userServiceimpl = ApplicationContextUtil.getBean(UserServiceimpl.class);
            User user = userServiceimpl.getOne(userQueryWrapper);
            if(user==null){
                log.error("Could not find user by userid:"+userid);
                throw new RuntimeException();
            }
            user.setMapid(-1);
            user.setHasmap(0);
            userServiceimpl.saveOrUpdate(user,userQueryWrapper);

            return Response.builder().code(ResponseConstant.RES_OK).msg("成功结束游戏").data("").build();
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
