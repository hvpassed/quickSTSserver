package com.cwk.qserver.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Repository;
//只供地图场景中使用，战斗场景中用BattlePlay
@Data
@TableName("player")
public class Player {
    @JsonProperty("mapid")
    private int mapid;

    @JsonProperty("userid")
    private int userid;

    @JsonProperty("maxhp")
    public int maxhp=100;

    @JsonProperty("nowhp")
    public int nowhp=100;

    @JsonProperty("playing")
    public int playing=0;

    @JsonProperty("playPos")
    public String playpos="[-1,0]";

    @JsonProperty("cardids")
    public String cardids;

    @JsonProperty("cardplay")
    @TableField(value = "cardplay")
    public int cardplay=0;

    @JsonProperty("damagerecive")
    @TableField(value = "damagerecive")
    public int damagerecive=0;

    @JsonProperty("damageout")
    @TableField(value = "damageout")
    public int damageout=0;

}
