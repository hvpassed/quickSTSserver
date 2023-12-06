package com.cwk.qserver.bean.postBody;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.bean
 * @Author: chen wenke
 * @CreateTime: 2023-12-06 14:10
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class DeleteCardParam {
    @JsonProperty("userid")
    private int userid;

    @JsonProperty("mapid")
    private int mapid;

    @JsonProperty("cardid")
    private int cardid;

    @JsonProperty("cardindex")
    private int cardIndex;
}
