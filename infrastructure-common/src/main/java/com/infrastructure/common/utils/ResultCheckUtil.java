package com.infrastructure.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.infrastructure.common.InfraPocketAbstractException;
import com.infrastructure.common.InfrastructureAbsResponseEnum;
import com.infrastructure.common.PageResponse;
import com.infrastructure.common.Result;

import java.util.Collection;
import java.util.Objects;

/**
 * 与ResultUtil的区别是，不会抛异常
 */
public class ResultCheckUtil {


    public static <T> Result checkResult(Result<T> result, InfrastructureAbsResponseEnum failResponse, String errCode){
        if(result == null){
            throw new InfraPocketAbstractException(InfrastructureAbsResponseEnum.UNKNOWN_EXCEPTION);
        } else if (!result.isStatus()){
            if(Objects.equals(errCode,result.getStatusCode()) && failResponse != null){
                return Result.error(failResponse);
            }
            return Result.error(result.getStatusCode(),result.getMessage());
        } else if (failResponse != null){
            T obj = result.getResult();
            boolean empty = obj instanceof Collection ?
                    CollUtil.isEmpty((Collection<T>) result.getResult()) : result.getResult() instanceof String ?
                    StrUtil.isEmpty(result.getResult().toString()) :
                    result.getResult() == null;
            if(empty){
                return Result.error(failResponse);
            }
        }
        return null;
    }
    public static <T> boolean resultDetailNotPass(Result<T> result){
        if(result == null){
           return true;
        } else if (!result.isStatus()){
            return true;
        } else {
            T obj = result.getResult();
            boolean empty = obj instanceof Collection ?
                    CollUtil.isEmpty((Collection<T>) result.getResult()) : result.getResult() instanceof String ?
                    StrUtil.isEmpty(result.getResult().toString()) :
                    result.getResult() == null;

            return empty;
        }
    }
    public static <T> boolean resultNotPass(Result<T> result){
        if(result == null){
           return true;
        } else if (!result.isStatus()){
            return true;
        } else {
            return false;
        }
    }


    public static <T> Result checkResult(Result<T> result, InfrastructureAbsResponseEnum failResponse){
        if(result == null){
            return Result.error(InfrastructureAbsResponseEnum.UNKNOWN_EXCEPTION);
        } else if (!result.isStatus()){
            return Result.error(result.getStatusCode(),result.getMessage());
        } else if (failResponse != null){
            T obj = result.getResult();
            boolean empty = obj instanceof Collection ?
                    CollUtil.isEmpty((Collection<T>) result.getResult()) : result.getResult() instanceof String ?
                    StrUtil.isEmpty(result.getResult().toString()) :
                    result.getResult() == null;
            if(empty){
                return Result.error(failResponse);
            }
        }
        return null;
    }
    public static <T> Result checkResult(Result<T> result){
        return checkResult(result,null);
    }

    public static <T> Result checkResultPage(Result<PageResponse<T>> result){
        return checkResultPage(result,null);
    }

    public static <T> Result checkResultPage(Result<PageResponse<T>> result, InfrastructureAbsResponseEnum failResponse){
        if(result == null){
            return Result.error(InfrastructureAbsResponseEnum.UNKNOWN_EXCEPTION);
        } else if (!result.isStatus()){
            return Result.error(result.getStatusCode(),result.getMessage());
        } else if (failResponse != null && (result.getResult() == null || CollUtil.isEmpty(result.getResult().getResult()))){
            //因为1.0.0版本有个bug，参数设置反了
            return Result.error(failResponse.getCode(),failResponse.getMsg());
        }
        return null;
    }

    public static <T> Result checkResultPage(Result<PageResponse<T>> result, InfrastructureAbsResponseEnum failResponse, String errCode){
        if(result == null){
            return Result.error(InfrastructureAbsResponseEnum.UNKNOWN_EXCEPTION);
        } else if (!result.isStatus()){
            if(Objects.equals(errCode,result.getStatusCode()) && failResponse != null){
                //因为1.0.0版本有个bug，参数设置反了
                return Result.error(failResponse.getCode(),failResponse.getMsg());
            }
            return Result.error(result.getStatusCode(),result.getMessage());
        } else if (failResponse != null && (result.getResult() == null || CollUtil.isEmpty(result.getResult().getResult()))){
            //因为1.0.0版本有个bug，参数设置反了
                return Result.error(failResponse.getCode(),failResponse.getMsg());
        }
        return null;
    }
}
