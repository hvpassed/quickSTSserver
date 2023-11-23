package com.cwk.qserver.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cwk.qserver.target.BattlePlayer;
import com.cwk.qserver.target.monster;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@TableName("battle")
public class Battle {

    @JsonProperty("userid")
    public int userid;//主键

    @JsonProperty("seed")
    public int seed;

    @JsonProperty("monsters")
    public List<monster> monsters;

    @JsonProperty("battleplayer")
    public BattlePlayer battlePlayer = new BattlePlayer();

}
