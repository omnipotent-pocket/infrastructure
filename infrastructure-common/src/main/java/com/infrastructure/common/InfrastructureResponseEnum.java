package com.infrastructure.common;

import lombok.Getter;

@Getter
public enum InfrastructureResponseEnum implements InfrastructureResponseCode {

    SUCCESS("200", "成功"),
    PARAM_ERROR("400", "参数错误"),
    SYSTEM_ERROR("500", "系统异常"),
    UNKNOWN_EXCEPTION("999", "未知异常");

    private final String code;
    private final String msg;

    InfrastructureResponseEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
