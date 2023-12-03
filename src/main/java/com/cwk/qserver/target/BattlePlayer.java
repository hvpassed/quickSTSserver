package com.cwk.qserver.target;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cwk.qserver.card.CardsPile;
import com.cwk.qserver.dao.entity.Player;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@TableName("battle_players")
@Data
public class BattlePlayer  {

    @JsonProperty("userid")
    @TableId(value = "userid",type = IdType.INPUT)
    public int userid;


    @JsonProperty("maxhp")
    public int maxhp=100;

    @JsonProperty("nowhp")
    public int nowhp=100;

    @JsonProperty("playPos")
    public String playpos="[-1,0]";

    @JsonProperty("block")
    public int block=0;
    @JsonProperty("seed")
    public int seed;

    @JsonProperty("all_pile")
    public String allPileString;
    @JsonProperty("draw_pile")
    public String drawPileString;

    @JsonProperty("hand_pile")
    public String handPileString;

    @JsonProperty("discord_pile")
    public String discordPileString;

    @JsonProperty("cost")
    public int cost =3;
    //总牌堆
    @TableField(exist = false)
    public List<Integer> allPile = new ArrayList<>();
    //抽牌堆，存储cardid，反射实例化类，
    @TableField(exist = false)
    public List<Integer> drawPile = new ArrayList<>();
    //手牌对
    @TableField(exist = false)
    public List<Integer> handPile = new ArrayList<>();
    //弃牌堆
    @TableField(exist = false)
    public List<Integer> discordPile = new ArrayList<>();


    public BattlePlayer(){

    }
    public BattlePlayer(Player player){
        this.userid= player.getUserid();
        this.maxhp = player.getMaxhp();
        this.nowhp = player.getNowhp();
    }


    public void serialize() {
        this.allPileString = CardsPile.serialize(allPile);
        this.drawPileString = CardsPile.serialize(drawPile);
        this.handPileString = CardsPile.serialize(handPile);
        this.discordPileString = CardsPile.serialize(discordPile);
    }
    public void unSerialize(){
        try {
            this.allPile = CardsPile.unSerialize(allPileString);
            this.drawPile = CardsPile.unSerialize(drawPileString);
            this.handPile = CardsPile.unSerialize(handPileString);
            this.discordPile = CardsPile.unSerialize(discordPileString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




//    public CardsPile cardsPile;//全部的牌
    public void ApplyDamage(int damage){

    }

}
