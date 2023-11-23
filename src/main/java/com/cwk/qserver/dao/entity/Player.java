package com.cwk.qserver.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@TableName("player")
public class Player {
    @JsonProperty("mapid")
    public int mapid;

    @JsonProperty("userid")
    public int userid;

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
}
