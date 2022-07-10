package com.panda.rpc.entity.exception;

import com.panda.rpc.entity.enumeration.RpcError;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/05/27/17:48
 * @Description:
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcError error, String detail){
        super(error.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause){
        super(message, cause);
    }

    public RpcException(RpcError error){
        super(error.getMessage());
    }
}
