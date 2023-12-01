package com.cwk.qserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cwk.qserver.card.Card;
import com.cwk.qserver.card.CardsPile;
import com.cwk.qserver.card.factory.CardsPileFactory;
import com.cwk.qserver.dao.IService.impl.UserServiceimpl;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.service.battle.BattleManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
@Service
class QServerApplicationTests {
    @Autowired
    private UserServiceimpl userServiceimpl;
    @Autowired
    private CardsPileFactory cardsPileFactory;
    @Test
    public void testBattleGene(){
        try {
            BattleManager.BattleManagerInitAndSava(1,1,"[0,0]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testDrawCards()
    {
        int [] nums = {0,1,2,3,4,5,6};
        int [] nums2 = {7,8,9,10};
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            list.add(nums[i]);
        }
        List<Integer> list1 = new ArrayList<>();
        for (int i = 0; i < nums2.length; i++) {
            list1.add(nums2[i]);
        }
        System.out.println(CardsPile.drawCards(list,list1,20,new Random(1)));

    }
    @Test
    public void testShuffle(){
        int [] nums = {0,1,2,3,4,5,6};
        int [] nums2 = {7,8,9,10};
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            list.add(nums[i]);
        }
        List<Integer> list1 = new ArrayList<>();
        for (int i = 0; i < nums2.length; i++) {
            list1.add(nums2[i]);
        }
        CardsPile.shuffle(list,new Random(2));

        System.out.println(list);
        CardsPile.shuffle(list,list1,new Random(3));
        System.out.println(list.remove(0));
        System.out.println(list);
        System.out.println(list1);
    }

    @Test
    public void monsterinittest(){

        try {
            Monster.InitMonsters(new Random(1),3,"[0,0]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStringToList(){
        String json = "{\"a\":[1,2,3,4,4,4,4,4]}";
        JSONObject jsonObject = JSON.parseObject(json);

        System.out.println(jsonObject.toString());
    }
    @Test

    public void test(){
        try {
            Card card = new Card();
            card.setTitle("123");
            card.setCardid(2);
            CardsPile cardsPile = cardsPileFactory.getObject();
            CardsPile cardsPile1 =cardsPileFactory.getObject();
            System.out.println(cardsPile.serialize());
            cardsPile.cardList.add(card);
            System.out.println(cardsPile.serialize());
            System.out.println(cardsPile1.serialize());
            System.out.println(cardsPile1.unSerialize("{\"Array\":[1,2,3,4,4,4,4,4]}"));

        }catch (Exception err){
            System.out.println(err.toString());
        }
    }
}
