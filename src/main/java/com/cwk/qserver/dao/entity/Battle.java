package com.cwk.qserver.dao.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//数据持久化
//每次出牌。或者对局变化，需从数据库实例化该类，对数据进行处理
@Data
@TableName("battle")
public class Battle {

    @JsonProperty("userid")
    @TableId(value = "userid")
    public int userid;//主键，每个用户只能有一个战斗

    @JsonProperty("curpos")
    public String curpos;//现在的位置

    @JsonProperty("seed")//战斗长久所用的种子
    public long seed;

    @JsonProperty("monsters")//存储当前战斗场景怪物id，每个怪物实体都有唯一id，其属性存储在数据库中，通过数据库数据实例化
    public String monsters;


    @TableField(exist = false)
    public List<Integer> monstersIdList=new ArrayList<>();

    @TableField(exist = false)
    public List<Monster> monstersList=new ArrayList<>();
    public void serialize(){
        this.monsters= Monster.serializeByMonsters(monstersList);
    }


}
