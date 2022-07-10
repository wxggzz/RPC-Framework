package com.panda.rpc;

import com.panda.rpc.serializer.CommonSerializer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/01/18:03
 * @Description:服务端类通用接口
 */
public interface RpcServer {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    void start() throws InterruptedException;
    //设置序列化器

    /**
     * @description 向Nacos注册服务
     * @param service, serviceClass]
     * @return [void]
     * @date [2021-03-13 15:56]
     */
    <T> void publishService(T service, String serviceName);

}
