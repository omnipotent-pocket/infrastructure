package com.infrastructure.common;

public enum InfrastructureAbsResponseEnum implements InfrastructureResponseCode {

    SUCCESS("200", "成功"),
    PARAM_ERROR("400", "参数错误"),
    LOGIC_ERROR("401", "逻辑异常"),
    LOGIN_ERROR("402", "用户未登录"),
    SYSTEM_ERROR("500", "系统异常"),
    RATE_LIMITER("503", "eden限流"),
    DATE_FORMAT_ERROR("504", "日期转换错误"),
    DATEBASE_EXEC_FAIL("600", "数据库操作失败"),
    DATEBASE_INSERT_FAIL("601", "数据库新增失败"),
    DATEBASE_UPDATE_FAIL("602", "数据库更新失败"),
    DATEBASE_DELETE_FAIL("603", "数据库删除失败"),
    DATE_NOT_FOUND("604", "未查询到数据"),
    RPC_CALL_FAIL("604", "接口调用失败"),
    THIRD_CALL_FAIL("605", "调用第三发异常"),
    UNKNOWN_EXCEPTION("999", "未知异常");

    private final String code;
    private final String msg;

    InfrastructureAbsResponseEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
