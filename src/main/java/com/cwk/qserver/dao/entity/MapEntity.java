package com.cwk.qserver.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@TableName("maps")
public class MapEntity {
    @JsonProperty("mapid")
    @TableId(value = "mapid",type= IdType.AUTO)
    private int mapid;

    @JsonProperty("userid")
    private int userid;

    @JsonProperty("currentposition")
    private String currentposition;

    @JsonProperty("seed")
    private long seed;

    @JsonProperty("endPos")
    @TableField(value = "endPos")
    private String endPos;

}
