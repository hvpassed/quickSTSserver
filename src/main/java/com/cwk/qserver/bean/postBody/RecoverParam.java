package com.cwk.qserver.bean.postBody;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.bean.postBody
 * @Author: chen wenke
 * @CreateTime: 2023-12-06 14:27
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class RecoverParam {
    @JsonProperty("userid")
    private int userid;

    @JsonProperty("mapid")
    private int mapid;

    @JsonProperty("value")
    private int value;
}
