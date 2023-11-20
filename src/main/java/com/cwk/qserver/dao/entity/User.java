package com.cwk.qserver.dao.entity;/*
 * @ author cwk
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@TableName("user")
public class User {

    @JsonProperty("id")

    public int id;

    @TableId(value ="username")
    @JsonProperty("username")
    public String username;

    @JsonProperty("password")
    public String password;
}
