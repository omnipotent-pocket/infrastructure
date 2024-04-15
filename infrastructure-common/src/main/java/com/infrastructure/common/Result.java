package com.infrastructure.common;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean status = false;

    private String message;

    private T result;

    private String statusCode;

    public static <T> Result<T> error(String statusCode, String message) {
        return new Result<>(message, null, statusCode);
    }

    public static <T> Result<T> error(InfrastructureResponseCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> Result<T> success(T data, InfrastructureResponseCode successCode) {
        return new Result<>(true, successCode.getMsg(), data, successCode.getCode());
    }

    public static <T> Result<T> success(T data) {
        return success(data, InfrastructureResponseEnum.SUCCESS);
    }

    public static <T> Result<T> success() {
        return success(null, InfrastructureResponseEnum.SUCCESS);
    }

    public Result() {
        super();
    }

    public Result(String message, T result, String statusCode) {
        this.message = message;
        this.result = result;
        this.statusCode = statusCode;
    }

    public Result(boolean status, String message, T result, String statusCode) {
        this.status = status;
        this.message = message;
        this.result = result;
        this.statusCode = statusCode;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

}
