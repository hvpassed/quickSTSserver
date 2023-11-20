package com.cwk.qserver.constant;

public class ResponseConstant {
    public static Integer RES_OK = 200;             // 响应正常，且有值可以返回
    public static Integer RES_NO_DATA = 201;        // 响应正常，但无数据
    public static Integer RES_NEED_CHECK = 400;     // 表示未被以下错误码分类的未知错误，msg中需要附加错误的简要信息，详细情况需要检查日志
    // 注：如果是经常发生的错误/异常，建议在下面新建常量
    public static Integer RES_ILLEGAL_PARAM = 401;  // 非法参数
    public static Integer RES_ILLEGAL_ACTION = 402;  // 非法操作，比如某些操作的前置条件不满足。
    public static Integer RES_TOO_MANY_REQUESTS = 403;  // 前端请求过于频繁。注：一般发生于线程池饱和的时候，捕捉线程池的相关异常
    public static Integer RES_NOT_FOUND = 404;  // 资源耗尽。注：一般发生于线程池饱和的时候，捕捉线程池的相关异常

    public static Integer RES_SERVER_ERROR = 500;   // 服务器错误或异常，需要检查日志，进行异常的标注
    // 其实这个状态码用不到，真正发生服务器异常的时候返回的应该是http状态码500了
}
