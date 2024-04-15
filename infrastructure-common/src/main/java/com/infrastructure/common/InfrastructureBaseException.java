package com.infrastructure.common;

import lombok.Getter;

@Getter
public class InfrastructureBaseException extends RuntimeException {

    private final String code;
    private final String msg;

    /**
     * 不推荐的使用方法，如需抛出系统异常，请或派生异常
     */
    public InfrastructureBaseException() {
        super(InfrastructureResponseEnum.SYSTEM_ERROR.getMsg());
        this.code = InfrastructureResponseEnum.SYSTEM_ERROR.getCode();
        this.msg = InfrastructureResponseEnum.SYSTEM_ERROR.getMsg();
    }

    public InfrastructureBaseException(InfrastructureResponseCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public InfrastructureBaseException(InfrastructureResponseCode errorCode, Throwable cause) {
        super(errorCode.getMsg(), cause);
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public InfrastructureBaseException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public InfrastructureBaseException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
