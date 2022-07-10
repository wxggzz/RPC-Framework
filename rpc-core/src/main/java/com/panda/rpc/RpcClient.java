package com.panda.rpc;

import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.serializer.CommonSerializer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/01/17:50
 * @Description:客户端类通用接口
 */
public interface RpcClient {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);
}