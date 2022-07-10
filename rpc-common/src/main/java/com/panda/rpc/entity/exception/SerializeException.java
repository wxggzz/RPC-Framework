package com.panda.rpc.entity.exception;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/04/22:31
 * @Description:
 */
public class SerializeException extends RuntimeException{
    public SerializeException(String msg){
        super(msg);
    }
}
