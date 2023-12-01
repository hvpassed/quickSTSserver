package com.cwk.qserver.dao.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cwk.qserver.card.Card;
import com.cwk.qserver.dao.IService.impl.MonsterServiceimpl;
import com.cwk.qserver.dao.Intent;
import com.cwk.qserver.utils.ApplicationContextUtil;
import com.cwk.qserver.utils.IsMonster;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.reflections.Reflections;

import java.util.*;

@Data
@TableName("monsters")
public class Monster {
    @JsonProperty("monsterid")
    @TableId(value = "monsterid",type = IdType.INPUT)
    public int monsterid;

    @JsonProperty("name")
    public String name;
    @JsonProperty("description")
    public String description;

    @JsonProperty("type")
    public int type;
    @JsonProperty("maxhp")
    public int maxhp;
    @JsonProperty("nowhp")
    public int nowhp;
    @JsonProperty("seed")
    public long seed;
    @JsonProperty("block")
    public int block=0;

    @JsonProperty("difficulty")
    public int difficulty;
    public Monster(){

    }

    public Monster(String name,String description,int basehp,int delta,int addOrDe,int type,String pos){
        String json = String.format("{\"Array\":%s}",pos);
        JSONObject jsonObj = JSON.parseObject(json);
        List<Integer> posl = jsonObj.getObject("Array", List.class);
        this.difficulty =posl.get(0);
        QueryWrapper<Monster> wrapper = Wrappers.query();
        wrapper.orderByDesc("monsterid").last("LIMIT 1");
        Monster res = ApplicationContextUtil.getBean(MonsterServiceimpl.class).getOne(wrapper);
        if(res==null){
            this.monsterid=0;
        }else {
            this.monsterid = res.monsterid + 1;
        }
        this.name = name;
        this.description = description;
        if(addOrDe==0){
            this.maxhp = basehp+delta;
            this.nowhp=this.maxhp;
        }else{
            this.maxhp = basehp-delta;
            this.nowhp=this.maxhp;
        }
        this.type=type;
        MonsterServiceimpl monsterService = ApplicationContextUtil.getBean(MonsterServiceimpl.class);

        monsterService.saveOrUpdate(this);


    }
    public void ApplyDamage(Card card){

    }

    public Intent generateIntent(){
        return new Intent();
    }

    public static String serializeByMonsters(List<Monster> monsters){
        List<Integer> list = new ArrayList<>();
        for (Monster monster:monsters
             ) {
            list.add(monster.monsterid);
        }
        Map<String,List<Integer>> ret= new HashMap<>();
        ret.put("Array",list);
        return JSON.toJSONString(ret);
    }

    public static String serializeByIds(List<Integer> monstersId){
        Map<String,List<Integer>> map = new HashMap<>();
        map.put("Array",monstersId);
        return JSON.toJSONString(map);
    }

    public static List<Integer> unSerialize(String json) throws Exception{
        JSONObject jsonObject = JSON.parseObject(json);
        List ret = new ArrayList<>();
        if(jsonObject.containsKey("Array")){
            ret = jsonObject.getObject("Array",ret.getClass());
            return ret;
        }else{
            throw new Exception();
        }
    }
    public static List<Monster> InitMonsters(Random random,int amount,String pos) throws Exception {
        try {
            //获取所有怪物类
            Reflections reflections = new Reflections("com.cwk.qserver.target.monsterimpl");
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(IsMonster.class);
            List<Class<?>> declaredMonster = new ArrayList<>(annotatedClasses);
            System.out.println((declaredMonster));

            List<Monster> monsters = new ArrayList<>();

            for(int i = 0;i<amount;i++){

                int index = random.nextInt(declaredMonster.size());
                //Monster m = (Monster) declaredMonster.get(index).getDeclaredConstructor(int.class,int.class).newInstance(0,0);
                int addOrDe = random.nextInt(2);
                int basehp = declaredMonster.get(index).getField("basehp").getInt(null);
                int delta = random.nextInt((int) (basehp*0.5));
                Monster monster = (Monster) declaredMonster.get(index).getDeclaredConstructor(int.class,int.class,String.class).newInstance(delta,addOrDe,pos);
                monster.seed = random.nextInt();
                monsters.add(monster);

            }
            System.out.println(monsters);
            return monsters;
        }catch (Exception e){
            throw e;
        }
    }

}
