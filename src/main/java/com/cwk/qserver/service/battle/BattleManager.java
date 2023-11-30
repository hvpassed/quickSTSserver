package com.cwk.qserver.service.battle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cwk.qserver.card.CardsPile;
import com.cwk.qserver.dao.IService.impl.*;
import com.cwk.qserver.dao.entity.Battle;
import com.cwk.qserver.dao.entity.MapEntity;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.dao.entity.Player;
import com.cwk.qserver.target.BattlePlayer;
import com.cwk.qserver.utils.ApplicationContextUtil;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

//战斗管理
@Getter
@Slf4j
@Data
public class BattleManager {
    //这些数据从battle中读取，修改也同步通过battle保存入数据库
    private long seed;
    private PlayerServiceimpl playerService;
    //场景中操作的对象
    public BattlePlayer battlePlayer;

    public List<Integer> monstersList=new ArrayList<>();
    //总牌堆
    public List<Integer> allPile = new ArrayList<>();
    //抽牌堆，
    public List<Integer> drawPile = new ArrayList<>();
    //手牌对
    public List<Integer> handPile = new ArrayList<>();
    //弃牌堆
    public List<Integer> discordPile = new ArrayList<>();

    public static void BattleManagerInitAndSava(int userid,int mapid,String pos) throws Exception{//进入战斗场景时，调用该方法，初始化战斗场景，并持久化
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
        //该场战斗的种子
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
        List<Monster>  createdMonsters = Monster.InitMonsters(random, monsterAmount);
        battle.monsters = Monster.serializeByMonsters(createdMonsters);

        battlePlayer.drawPile = dp.get("drawPile");
        battlePlayer.handPile = dp.get("handPile");
        battlePlayer.discordPile = dp.get("discordPile");
        battlePlayer.allPile =allPile;

        battlePlayer.serialize();


        log.info(String.format("Save into database:Battle info %s ,Monsters info:%s",battle.toString(),createdMonsters.toString()));
        System.out.println(battlePlayer);
        //持久化
        BattleServiceimpl battleService = ApplicationContextUtil.getBean(BattleServiceimpl.class);
        battleService.saveOrUpdate(battle);


        BattlePlayerServiceimpl battlePlayerServiceimpl = ApplicationContextUtil.getBean(BattlePlayerServiceimpl.class);
        battlePlayerServiceimpl.saveOrUpdate(battlePlayer);
        log.info("Initiated.");


    }
    BattleManager(){

    }



}
