package com.cwk.qserver.service.battle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cwk.qserver.card.Card;
import com.cwk.qserver.card.CardsPile;
import com.cwk.qserver.card.factory.CardFactory;
import com.cwk.qserver.dao.IService.BattlePlayerService;
import com.cwk.qserver.dao.IService.BattleService;
import com.cwk.qserver.dao.IService.impl.*;
import com.cwk.qserver.dao.Intent;
import com.cwk.qserver.dao.entity.Battle;
import com.cwk.qserver.dao.entity.MapEntity;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.dao.entity.Player;
import com.cwk.qserver.target.BattlePlayer;
import com.cwk.qserver.utils.ApplicationContextUtil;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

//战斗管理
@Getter
@Slf4j
@Data
public class BattleManager {
    //这些数据从battle中读取，修改也同步通过battle保存入数据库
    private Battle battle;
    private long seed;
    private PlayerServiceimpl playerService;
    //场景中操作的对象
    public BattlePlayer battlePlayer;

    public List<Integer> monstersList;
    private int userid;
//    //总牌堆
//    public List<Integer> allPile = new ArrayList<>();
//    //抽牌堆，
//    public List<Integer> drawPile = new ArrayList<>();
//    //手牌对
//    public List<Integer> handPile = new ArrayList<>();
//    //弃牌堆
//    public List<Integer> discordPile = new ArrayList<>();

    public static Map<String,Object> BattleManagerInitAndSava(int userid,int mapid,String pos) throws Exception{//进入战斗场景时，调用该方法，初始化战斗场景，并持久化
        log.info(String.format("Userid:%d entering %s,Initiating battle,monsters,player",userid,pos));
        Battle battle = new Battle();
        //读取player数据，
        PlayerServiceimpl playerService =  ApplicationContextUtil.getBean(PlayerServiceimpl.class);
        MapServiceimpl mapService = ApplicationContextUtil.getBean(MapServiceimpl.class);
        QueryWrapper<MapEntity> mapQueryWrapper = Wrappers.query();
        mapQueryWrapper.eq("userid",userid).eq("mapid",mapid);
        MapEntity mapEntity = mapService.getOne(mapQueryWrapper);
        if(mapEntity ==null){
            log.error("Could not find map by userid:"+userid+" mapid:"+mapid);
            throw new Exception();
        }
        Random random = new Random(mapEntity.getSeed());
        String nj = String.format("{\"Array\":%s}",pos);
        JSONObject njo = JSON.parseObject(nj);
        List<Integer> ns = njo.getObject("Array",List.class);
        if(ns.size()!=2){
            throw new Exception();
        }
        //该场战斗的种子
        for(int i = 0;i<ns.get(0)*10+ns.get(1);i++){
            random.nextInt();
        }
        long seed = random.nextInt();
        battle.seed=seed;
        QueryWrapper<Player> playerQueryWrapper = Wrappers.query();
        playerQueryWrapper.eq("userid",userid);
        Player player = playerService.getOne(playerQueryWrapper);
        if(player==null){
            log.error("Could not find player by userid:"+userid);
            throw new Exception();
        }
        //牌堆处理，将初始化抽牌堆，抽排，初始化弃牌堆
        BattlePlayer battlePlayer=new BattlePlayer(player);
        battlePlayer.seed = random.nextInt();;
        battlePlayer.block=0;
        //从用户持有的牌中，初始化总牌堆
        List<Integer> allPile = CardsPile.unSerialize(player.cardids);
        CardsPile.shuffle(allPile,random);
        Map<String ,List<Integer>> dp = CardsPile.drawCards(allPile,new ArrayList<>(),5,random);

        battle.userid = userid;

        battle.curpos = pos;
        //生成怪物
        int monsterAmount = random.nextInt(4);
        if(monsterAmount==0){
            monsterAmount=1;
        }
        List<Monster>  createdMonsters = Monster.InitMonsters(random, monsterAmount,pos);
        battle.monsters = Monster.serializeByMonsters(createdMonsters);

        battlePlayer.drawPile = dp.get("drawPile");
        battlePlayer.handPile = dp.get("handPile");
        battlePlayer.discordPile = dp.get("discordPile");
        battlePlayer.allPile =allPile;

        battlePlayer.serialize();
        IntentServiceimpl intentService = ApplicationContextUtil.getBean(IntentServiceimpl.class);
        List<Intent> intents = new ArrayList<>();
        for (Monster monster:createdMonsters
             ) {

            Intent temp = monster.generateIntent();
            intents.add(temp);
            intentService.saveOrUpdate(temp);
        }

        log.info(String.format("Save into database:Battle info %s ,Monsters info:%s",battle.toString(),createdMonsters.toString()));
        System.out.println(battlePlayer);
        //持久化
        BattleServiceimpl battleService = ApplicationContextUtil.getBean(BattleServiceimpl.class);
        battleService.saveOrUpdate(battle);


        BattlePlayerServiceimpl battlePlayerServiceimpl = ApplicationContextUtil.getBean(BattlePlayerServiceimpl.class);
        battlePlayerServiceimpl.saveOrUpdate(battlePlayer);
        log.info("Initiated.");
        Map<String,Object> ret = new HashMap<>();
        ret.put("battle",battle);
        ret.put("monsters",createdMonsters);
        ret.put("battle_player",battlePlayer);
        ret.put("intents",intents);
        return ret;

    }
    public BattleManager(int userid) throws Exception {
        BattleServiceimpl battleService = ApplicationContextUtil.getBean(BattleServiceimpl.class);
        QueryWrapper<Battle> battleQueryWrapper = Wrappers.query();
        battleQueryWrapper.eq("userid",userid);
        Battle battle = battleService.getOne(battleQueryWrapper);
        if(battle==null){
            log.error("Could not find battle by userid:"+userid);
            throw new RuntimeException();
        }
        this.userid=userid;
        this.battle = battle;
        this.monstersList  =  Monster.unSerialize(battle.monsters);
        this.seed = battle.seed;
        BattlePlayerServiceimpl battlePlayerServiceimpl = ApplicationContextUtil.getBean(BattlePlayerServiceimpl.class);
        QueryWrapper<BattlePlayer> battlePlayerQueryWrapper = Wrappers.query();
        battlePlayerQueryWrapper.eq("userid",userid);
        this.battlePlayer = battlePlayerServiceimpl.getOne(battlePlayerQueryWrapper);
        if(this.battlePlayer==null){
            log.error("Could not find battle_player by userid:"+userid);
            throw new RuntimeException();
        }
        this.playerService = ApplicationContextUtil.getBean(PlayerServiceimpl.class);
    }

    public BattlePlayer CardImpactPlayer(int cardid) throws RuntimeException{
        //卡牌以玩家为目标

        CardFactory cardFactory = ApplicationContextUtil.getBean(CardFactory.class);
        Set<Integer> cardids = new HashSet<>();
        cardids.add(cardid);
        List<Card> cardList = cardFactory.createCardById(cardids);
        if(cardList.isEmpty()){
            log.error("Could not find card by cardid:"+cardid);
            throw new RuntimeException();
        }
        Card card = cardList.get(0);
        card.impact(battlePlayer);
        BattlePlayerServiceimpl battlePlayerServiceimpl = ApplicationContextUtil.getBean(BattlePlayerServiceimpl.class);
        battlePlayerServiceimpl.saveOrUpdate(battlePlayer);
        return this.battlePlayer;

    }
    public List<Monster> CardImpactAllMonster(int cardid) throws RuntimeException{
        //卡牌以全体为目标
        MonsterServiceimpl monsterService = ApplicationContextUtil.getBean(MonsterServiceimpl.class);
        List<Monster>  monsters = monsterService.listByIds(this.monstersList);
        if(monsters.isEmpty()){
            log.error("Could not find monster by monsterids:"+this.monstersList.toString());
            throw new RuntimeException();
        }
        CardFactory cardFactory = ApplicationContextUtil.getBean(CardFactory.class);
        Set<Integer> cardids = new HashSet<>();
        cardids.add(cardid);
        List<Card> cardList = cardFactory.createCardById(cardids);
        if(cardList.isEmpty()){
            log.error("Could not find card by cardid:"+cardid);
            throw new RuntimeException();
        }
        Card card = cardList.get(0);
        card.impactAll(monsters);
        monsterService.saveOrUpdateBatch(monsters);

        return monsters;
    }
    public Monster CardImpactMonster(int cardid,int monsterid) throws RuntimeException{
        MonsterServiceimpl monsterService = ApplicationContextUtil.getBean(MonsterServiceimpl.class);
        Monster monster = monsterService.getById(monsterid);
        if(monster==null){
            log.error("Could not find monster by monsterid:"+monsterid);
            throw new RuntimeException();
        }
        CardFactory cardFactory = ApplicationContextUtil.getBean(CardFactory.class);
        Set<Integer> cardids = new HashSet<>();
        cardids.add(cardid);
        List<Card> cardList = cardFactory.createCardById(cardids);
        if(cardList.isEmpty()){
            log.error("Could not find card by cardid:"+cardid);
            throw new RuntimeException();
        }
        Card card = cardList.get(0);

        card.impact(monster);

        monsterService.saveOrUpdate(monster);

        return monster;
    }

}
