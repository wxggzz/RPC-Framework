package com.panda.rpc.entity;

import com.panda.rpc.entity.enumeration.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 服务端返回的信息  有状态码，信息，和数据。由于需要序列化传输，所以也要实现SE
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {
    private Integer statusCode;
    private String message;
    private T data;


    /**
     * 响应对应的请求号
     */
    private String requestId;
    /**
     * @description 成功时服务端返回的对象
     * @param data
     * @return [com.panda.rpc.entity.RpcResponse<T>]
     * @date [2021-02-03 17:31]
     */
    public static <T> RpcResponse<T> success(T data, String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setRequestId(requestId);
        response.setData(data);
        return response;
    }
    /**
     * @description 失败时服务端返回的对象
     * @param code
     * @return [com.panda.rpc.entity.RpcResponse<T>]
     * @date [2021-02-03 17:42]
     */
    public static <T> RpcResponse<T> fail(ResponseCode code ,String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setRequestId(requestId);
        response.setMessage(code.getMessage());
        return response;
    }
}
