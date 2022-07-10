package com.panda.rpc.test;

import com.panda.rpc.RpcServer;
import com.panda.rpc.annotation.ServiceScan;
import com.panda.rpc.api.HelloService;
import com.panda.rpc.netty.server.NettyServer;
import com.panda.rpc.provider.ServiceProviderImpl;
import com.panda.rpc.serializer.CommonSerializer;
import com.panda.rpc.serializer.JsonSerializer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/03/0:13
 * @Description:
 */
@ServiceScan
public class NettyTestServer {
    /**
     * 1.首先创建服务
     * 2.注册服务
     * 3.创建服务端
     * 4.启动
     */
    public static void main(String[] args) throws InterruptedException {
//        HelloServiceImpl helloService = new HelloServiceImpl();
//        //都继承了rpcServer 实现netty和socket的 实现rpcServer接口就行
//        NettyServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
//        server.publishService(helloService, HelloService.class);
        RpcServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.KRYO_SERIALIZER);
        server.start();
    }

}
