package com.infrastructure.common;

import lombok.Getter;

@Getter
public class InfraPocketAbstractException extends RuntimeException {

    private final String code;
    private final String msg;

    /**
     * 不推荐的使用方法，如需抛出系统异常，请或派生异常
     */
    public InfraPocketAbstractException() {
        super(InfrastructureAbsResponseEnum.SYSTEM_ERROR.getMsg());
        this.code = InfrastructureAbsResponseEnum.SYSTEM_ERROR.getCode();
        this.msg = InfrastructureAbsResponseEnum.SYSTEM_ERROR.getMsg();
    }

    public InfraPocketAbstractException(InfrastructureResponseCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public InfraPocketAbstractException(InfrastructureResponseCode errorCode, Throwable cause) {
        super(errorCode.getMsg(), cause);
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public InfraPocketAbstractException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public InfraPocketAbstractException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
