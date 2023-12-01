package com.cwk.qserver.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.dao
 * @Author: chen wenke
 * @CreateTime: 2023-12-01 12:45
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName(value = "intents")
public class Intent {

    @JsonProperty("monsterid")
    @TableId(value = "monsterid",type = IdType.INPUT)
    public int monsterid;

    @JsonProperty("aims")
    @TableField(exist = false)
    public final List<String> aims = List.of("ATTACK","DEFEND","SKILL");

    @JsonProperty("aim")
    public String aim;

    @JsonProperty("defend")
    public int blockGain;

    @JsonProperty("attack_amount")
    public int attack_amount;

    @JsonProperty("attack")
    public int attack;
}
