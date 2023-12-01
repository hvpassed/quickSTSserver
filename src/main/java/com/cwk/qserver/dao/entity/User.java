package com.cwk.qserver.dao.entity;/*
 * @ author cwk
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@TableName("user")
public class User {

    @JsonProperty("userid")
    public int userid;

    @TableId(value ="username")
    @JsonProperty("username")
    public String username;

    @JsonProperty("password")
    public String password;

    @JsonProperty("mapid")
    public int mapid;

    @JsonProperty("hasmap")
    public int hasmap;

    @JsonProperty("enter_pos")
    @TableField(exist = false)
    public String enterPos;
}
