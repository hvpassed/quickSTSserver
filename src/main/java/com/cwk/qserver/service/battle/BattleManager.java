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
import com.cwk.qserver.dao.entity.*;
import com.cwk.qserver.target.BattlePlayer;
import com.cwk.qserver.utils.ApplicationContextUtil;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public static Map<String,Object> reInitBattle(int userid) throws Exception {
        log.info(String.format("Reinit battle userid:"+userid));
        BattleServiceimpl battleServiceimpl = ApplicationContextUtil.getBean(BattleServiceimpl.class);
        QueryWrapper<Battle> battleQueryWrapper = Wrappers.query();
        battleQueryWrapper.eq("userid",userid);
        Battle battle = battleServiceimpl.getOne(battleQueryWrapper);
        if(battle==null){
            log.error("Could not find battle by userid:"+userid);
            throw new RuntimeException();
        }
        battleServiceimpl.remove(battleQueryWrapper);
        List<Integer> ml = Monster.unSerialize(battle.monsters);
        MonsterServiceimpl monsterServiceimpl = ApplicationContextUtil.getBean(MonsterServiceimpl.class);
        monsterServiceimpl.removeBatchByIds(ml);
        String pos = battle.curpos;
        //读取player数据，
        PlayerServiceimpl playerService =  ApplicationContextUtil.getBean(PlayerServiceimpl.class);
        MapServiceimpl mapService = ApplicationContextUtil.getBean(MapServiceimpl.class);
        QueryWrapper<MapEntity> mapQueryWrapper = Wrappers.query();
        mapQueryWrapper.eq("userid",userid);
        MapEntity mapEntity = mapService.getOne(mapQueryWrapper);
        int mapid = mapEntity.getMapid();
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
        Map<String ,Object> dp = CardsPile.drawCards(allPile,new ArrayList<>(),new ArrayList<>(),battlePlayer.getDrawAmount(),random);

        battle.userid = userid;

        battle.curpos = pos;
        //生成怪物
        int monsterAmount = random.nextInt(4);
        if(monsterAmount==0){
            monsterAmount=1;
        }
        List<Monster>  createdMonsters = Monster.InitMonsters(random, monsterAmount,pos);
        battle.monsters = Monster.serializeByMonsters(createdMonsters);

        battlePlayer.drawPile = (List<Integer>) dp.get("drawPile");
        battlePlayer.handPile = (List<Integer>) dp.get("handPile");
        battlePlayer.discordPile = (List<Integer>) dp.get("discordPile");
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
        Map<String ,Object> dp = CardsPile.drawCards(allPile,new ArrayList<>(),new ArrayList<>(),battlePlayer.getDrawAmount(),random);

        battle.userid = userid;

        battle.curpos = pos;
        //生成怪物
        int monsterAmount = random.nextInt(4);
        if(monsterAmount==0){
            monsterAmount=1;
        }
        List<Monster>  createdMonsters = Monster.InitMonsters(random, monsterAmount,pos);
        battle.monsters = Monster.serializeByMonsters(createdMonsters);

        battlePlayer.drawPile = (List<Integer>) dp.get("drawPile");
        battlePlayer.handPile = (List<Integer>) dp.get("handPile");
        battlePlayer.discordPile = (List<Integer>) dp.get("discordPile");
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
        this.monstersList  =  Monster.unSerialize(battle.getMonsters());
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


    public Map<String,Object> endTurn(int userid) throws RuntimeException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        IntentServiceimpl intentService = ApplicationContextUtil.getBean(IntentServiceimpl.class);
        List<Monster> monsters  = new ArrayList<>();
        Map<Integer,Class<?>> monsterType = Monster.getMonsterTypeMap();
        Map<Integer,Intent> IntentList = new HashMap<>();
        for(int i = 0;i<this.monstersList.size();i++){
            MonsterServiceimpl monsterService = ApplicationContextUtil.getBean(MonsterServiceimpl.class);
            Monster monsterObj = monsterService.getById(this.monstersList.get(i));
            if(monsterObj.getNowhp()>0) {
                Class<?> monster = monsterType.get(monsterObj.getType());

                if (monster == null) {
                    log.error("Could not find monster by monsterid:" + this.monstersList.get(i));
                    throw new RuntimeException();
                }
                Object obj = monster.getDeclaredConstructor(Monster.class).newInstance(monsterObj);
                if (obj == null) {
                    log.error("Could not find monster by monsterid:" + this.monstersList.get(i));
                    throw new RuntimeException();
                }
                Method clearBlock = monster.getMethod("clearBlock");
                if (clearBlock == null) {
                    log.error("Could not find method clearBlock in monster:" + monster.getName());
                    throw new RuntimeException();
                }
                clearBlock.invoke(obj);
                Method intentApply = monster.getMethod("IntentApply", Object.class);
                if (intentApply == null) {
                    log.error("Could not find method IntentApply in monster:" + monster.getName());
                    throw new RuntimeException();
                }

                intentApply.invoke(obj, battlePlayer);

                Method generateIntent = monster.getMethod("generateIntent");
                Intent newIntent = (Intent) generateIntent.invoke(obj);

                intentService.saveOrUpdate(newIntent);
                IntentList.put(monsterObj.getMonsterid(),newIntent);
                monsterService.saveOrUpdate((Monster) obj);
                monsters.add((Monster) obj);
            }
        }
        battlePlayer.clearBlock();
        BattlePlayerServiceimpl battlePlayerServiceimpl = ApplicationContextUtil.getBean(BattlePlayerServiceimpl.class);
        battlePlayerServiceimpl.saveOrUpdate(battlePlayer);
        Map<String,Object> ret = new HashMap<>();
        ret.put("battle_player",battlePlayer);
        ret.put("monsters",monsters);
        ret.put("intents",IntentList);
        return ret;
    }

    public Map<String,Object> winBattle(int userid) throws Exception {
        //删除怪物
        MonsterServiceimpl monsterService = ApplicationContextUtil.getBean(MonsterServiceimpl.class);
        monsterService.removeBatchByIds(this.monstersList);

        //删除battle
        BattleServiceimpl battleService = ApplicationContextUtil.getBean(BattleServiceimpl.class);
        battleService.removeById(userid);

        //更新player
        QueryWrapper<Player> playerQueryWrapper = Wrappers.query();
        playerQueryWrapper.eq("userid",userid);
        Player player = playerService.getOne(playerQueryWrapper);
        if(player==null){
            log.error("Could not find player by userid:"+userid);
            throw new RuntimeException();
        }
        player.setPlaying(0);

        List<Integer> allCardids =CardsPile.unSerialize( player.getCardids());
        playerService.saveOrUpdate(player,playerQueryWrapper);


        //更新map
        MapServiceimpl mapService = ApplicationContextUtil.getBean(MapServiceimpl.class);
        QueryWrapper<MapEntity> mapQueryWrapper = Wrappers.query();
        mapQueryWrapper.eq("userid",userid);
        MapEntity mapEntity = mapService.getOne(mapQueryWrapper);
        if(mapEntity==null){
            log.error("Could not find map by userid:"+userid);
            throw new RuntimeException();
        }

        mapEntity.setCurrentposition(player.playpos);
        mapService.saveOrUpdate(mapEntity);

        //回传 待择的卡牌
        Map<String,Object> ret = new HashMap<>();
        CardFactory cardFactory = ApplicationContextUtil.getBean(CardFactory.class);
        List<Integer> cardids = new ArrayList<>();
        List<Card> Cards = new ArrayList<>();
        for(int i = 2;i<CardFactory.allCards;i++){
            cardids.add(i);
        }
        CardsPile.shuffle(cardids,new Random(battle.getSeed()));
        for(int i = 0;i<3;i++)
        {
            Set<Integer> st = new HashSet<>();
            st.add(cardids.remove(0));
            Cards.add(cardFactory.createCardById(st).get(0));
        }
        List<Card> allCard = new ArrayList<>();
        for (Integer i:allCardids
             ) {
            Set<Integer> st = new HashSet<>();
            st.add(i);
            allCard.add(cardFactory.createCardById(st).get(0));
        }
        ret.put("SelectCards",Cards);
        ret.put("AllCards",allCard);
        return  ret ;
    }
}
