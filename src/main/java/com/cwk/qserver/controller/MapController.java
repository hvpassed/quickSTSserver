package com.cwk.qserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cwk.qserver.card.CardsPile;
import com.cwk.qserver.card.factory.CardsPileFactory;
import com.cwk.qserver.constant.MapConstant;
import com.cwk.qserver.constant.ResponseConstant;
import com.cwk.qserver.dao.IService.impl.*;
import com.cwk.qserver.dao.PlayMapUnion;
import com.cwk.qserver.dao.Response;
import com.cwk.qserver.dao.entity.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/map")
@Data
public class MapController {
    @Autowired
    private MapServiceimpl mapServiceimpl;

    @Autowired
    private UserServiceimpl userServiceimpl;
    @Autowired
    private PlayerServiceimpl playerServiceimpl;
    @Autowired
    private CardsPileFactory cardsPileFactory;
    @Autowired
    private MonsterServiceimpl monsterService;

    @Autowired
    private BattleServiceimpl battleService;
    @PostMapping("/initMap")
    @ResponseBody
    public Response initMap(@RequestBody MapEntity entity){
        try {
            log.info("query "+entity.toString());
            QueryWrapper<MapEntity> qw = Wrappers.query();
            qw.eq("userid",entity.getUserid());
            mapServiceimpl.saveOrUpdate(entity,qw);
            MapEntity res = mapServiceimpl.getOne(qw);
            if(res==null) {
                //创建地图失败
                return Response.builder().code(MapConstant.MAP_CREATED_FAILED).msg("MAP_CREATED_FAILED").data("").build();
            }else{
                QueryWrapper<User> uq = Wrappers.query();
                uq.eq("userid",entity.getUserid());
                User user = userServiceimpl.getOne(uq);
                if(user==null){
                    log.error("MAP_USER_UPDATE_FAILED");
                    mapServiceimpl.remove(qw);
                    return Response.builder().code(MapConstant.MAP_CREATED_FAILED).msg("MAP_USER_UPDATE_FAILED").data("").build();
                }else{
                    log.info("MAP_CREATED");
                    user.setHasmap(1);
                    user.setMapid(res.getMapid());
                    userServiceimpl.saveOrUpdate(user);
                    Player player = new Player();

                    player.setMapid(res.getMapid());
                    player.setUserid(res.getUserid());
                    QueryWrapper<Player> playerQueryWrapper = Wrappers.query();
                    playerQueryWrapper.eq("userid",res.getUserid()).eq("mapid",res.getMapid());
                    Player qur = playerServiceimpl.getOne(playerQueryWrapper);

                    CardsPile initpile = cardsPileFactory.getObject();
                    player.setCardids(initpile.serialize());
                    if(qur==null){

                        playerServiceimpl.save(player);
                    }else{
                        playerServiceimpl.saveOrUpdate(player,playerQueryWrapper);
                    }
                    PlayMapUnion ret = new PlayMapUnion();
                    ret.player = player;
                    ret.mapEntity =res;
                    return Response.builder().code(MapConstant.MAP_CREATED).msg("MAP_CREATED").data(ret).build();
                }

            }
        }catch (Exception err){
            log.error(err.toString());
            return  Response.builder().code(MapConstant.UNEXCEPTED_ERR).msg(err.toString()).data("").build();
        }
    }
    @PostMapping("read")
    @ResponseBody
    public  Response readMapInfo(@RequestBody User entity){
        try {

            QueryWrapper<User> uq = Wrappers.query();
            uq.eq("userid",entity.userid);
            User res = userServiceimpl.getOne(uq);
            if(res==null){
                log.error("a map missed");
                return Response.builder().code(MapConstant.UNEXCEPTED_ERR).msg("USER_MISSED").data("").build();
            }else{

                if(res.getHasmap()==1){
                    QueryWrapper<MapEntity> mq = Wrappers.query();
                    mq.eq("mapid",res.getMapid());
                    MapEntity mapEntity = mapServiceimpl.getOne(mq);
                    if(mapEntity ==null){
                        log.error("a map missed");
                        return Response.builder().code(MapConstant.UNEXCEPTED_ERR).msg("USER_MISSED").data("").build();
                    }else{

                        QueryWrapper<Player> pq = Wrappers.query();
                        pq.eq("mapid", mapEntity.getMapid()).eq("userid",entity.getUserid());
                        Player player = playerServiceimpl.getOne(pq);
                        if(player==null){
                            log.error("a player missed");
                            return Response.builder().code(MapConstant.UNEXCEPTED_ERR).msg("PLAYER_MISSED").data("").build();
                        }else{
                            PlayMapUnion playMapUnion = new PlayMapUnion();
                            playMapUnion.player=player;
                            playMapUnion.mapEntity = mapEntity;
                            return Response.builder().code(MapConstant.MAP_FOUNDED).msg("MAP_FOUNDED").data(playMapUnion).build();
                        }
                        //return Response.builder().code(MapConstant.MAP_FOUNDED).msg("MAP_FOUNDED").data(map).build();
                    }
                }else{
                    return Response.builder().code(MapConstant.UNEXCEPTED_ERR).msg("MAP_MISSED").data("").build();
                }
            }


        }catch (Exception err){
            log.error(err.toString());
            return Response.builder().code(MapConstant.UNEXCEPTED_ERR).msg(err.toString()).data("").build();
        }
    }
    @PostMapping("/updating")
    @ResponseBody
    public Response updateUserInfo(@RequestBody User entity){
        try {

            return Response.builder().build();

        }catch (Exception err){
            log.error(err.toString());
            return Response.builder().build();
        }


    }

    @PostMapping("/loseGame")
    @ResponseBody
    public Response loseGame(@RequestBody User entity){
        try {
            //删除map，更新user，删除monster；
            Battle battle = battleService.getById(entity.getUserid());
            if(battle!=null){
                //删除monster
                List<Integer> monsterList = Monster.unSerialize(battle.getMonsters());
                monsterService.removeBatchByIds(monsterList);
            }
            QueryWrapper<User> uq = Wrappers.query();
            uq.eq("userid",entity.getUserid());
            User user = userServiceimpl.getOne(uq);
            if(user==null){
                return Response.builder().code(ResponseConstant.RES_ILLEGAL_PARAM).msg("用户不存在").data("").build();
            }
            int mapid = user.getMapid();
            user.setHasmap(0);
            user.setMapid(-1);
            userServiceimpl.saveOrUpdate(user);
            UpdateWrapper<MapEntity> uw = Wrappers.update();
            uw.eq("userid",entity.getUserid()).eq("mapid",mapid);
            mapServiceimpl.remove(uw);
            return Response.builder().code(ResponseConstant.RES_OK).msg("成功删除地图").data("").build();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Response.builder().code(ResponseConstant.RES_ILLEGAL_PARAM).msg("未知错误："+e.getMessage()).data("").build();
        }
    }
}
