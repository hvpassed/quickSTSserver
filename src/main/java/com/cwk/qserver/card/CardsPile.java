package com.cwk.qserver.card;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.*;

@Data

public class CardsPile {
    public List<Card> cardList=new ArrayList<>();

    public CardsPile(List<Card> cardList){

        this.cardList.addAll(cardList);
    }
    public String serialize(){
        Map<String,List<Integer>> map = new HashMap<>();
        List<Integer> ids  = new ArrayList<>();
        for (Card card:cardList) {
            ids.add(card.getCardid());
        }
        map.put("Array",ids);
        return JSON.toJSONString(map);
    }
    public static void shuffle(List<Integer> pile,Random random){
//        for (int i=arr.size()-1;i>=0;--i)
//        {
//            srand((unsigned)time(NULL));
//            swap(arr[rand()%(i+1)],arr[i]);
//        }

        for (int i = pile.size()-1;i>=0;--i){
            int index = random.nextInt(i+1);
            int temp = pile.get(index);
            pile.set(index,pile.get(i));
            pile.set(i,temp);
        }

    }
    public static void shuffle(List<Integer> drawPile,List<Integer> discordPile,Random random){
        drawPile.addAll(discordPile);
        discordPile.clear();
        CardsPile.shuffle(drawPile,random);
    }
    public static String serialize(List<Integer> pile){
        Map<String,List<Integer>> map = new HashMap<>();
        map.put("Array",pile);
        return JSON.toJSONString(map);
    }
    public static Map<String,Object> drawCards(List<Integer> drawPile, List<Integer> discordPile, List<Integer> handPile, int amount, Random random){
        //从抽牌堆中抽牌，如果抽牌堆数量不够，则会重新洗牌抽卡，
        //返回抽牌堆，手牌堆，弃牌堆
        //将手牌堆放入弃牌堆
        int size = handPile.size();
        for (int i = 0; i < size; i++) {
            int ele = handPile.remove(0);
            discordPile.add(ele);
        }

        int at = Math.min(amount,drawPile.size()+discordPile.size());
        if(drawPile.size()<at && !discordPile.isEmpty()){
            //洗牌
            CardsPile.shuffle(drawPile,discordPile,random);
        }
        List<Integer> drawP = new ArrayList<>(List.copyOf(drawPile));
        List<Integer> disP =  new ArrayList<>(List.copyOf(discordPile));
        List<Integer> handP = new ArrayList<>();

        Map<String,Object> ret = new HashMap<>();

        for(int i = 0;i<at;i++){
            handP.add(drawP.get(i));
        }
        for(int i = 0;i<at;i++){
            drawP.remove(0);
        }
        ret.put("drawPile",drawP);
        ret.put("handPile",handP);
        ret.put("discordPile",disP);
        return ret;

    }
    public static List<Integer> unSerialize(String jsonStr) throws Exception{
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        List ret = new ArrayList<>();
        if(jsonObject.containsKey("Array")){
            ret = jsonObject.getObject("Array",ret.getClass());
            return ret;
        }else{
            throw new Exception();
        }
    }
}
