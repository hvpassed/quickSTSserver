package com.cwk.qserver.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class Response {
    private Integer code;
    private String msg;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private Date nowTime = new Date();
    private Object data;
}
