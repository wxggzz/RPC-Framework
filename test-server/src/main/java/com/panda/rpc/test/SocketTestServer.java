package com.panda.rpc.test;

import com.panda.rpc.RpcServer;
import com.panda.rpc.annotation.ServiceScan;
import com.panda.rpc.api.HelloService;
import com.panda.rpc.netty.server.NettyServer;
import com.panda.rpc.provider.ServiceProviderImpl;
import com.panda.rpc.provider.ServiceProvider;
import com.panda.rpc.serializer.CommonSerializer;
import com.panda.rpc.serializer.ProtostuffSerializer;
import com.panda.rpc.socket.server.SocketServer;
@ServiceScan
public class SocketTestServer {


    public static void main(String[] args) throws InterruptedException {
        RpcServer server = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
//        //创建服务对象
//        HelloService helloService = new HelloServiceImpl2();
//        //创建服务容器  ,容器用来保存服务的信息，通过服务名称就能找到服务的具体对象
//        ServiceProvider serviceProvider = new ServiceProviderImpl();
//        /**
//         * 1.注意是传入 实现类，然后
//         *2.是将实接口类的名称注册放入set
//         * 3.将实现类的接口还有实现类对象注册放入map,为了后面根据接口名获取 服务对象
//         */
//
//        //注册服务对象到服务容器中  ,获取实现类的接口名称
//
//        //将服务容器放入到服务端中，这时候会自动创建线程池
//        //服务的注册已经不由 RpcServer 处理了，它只需要启动容器服务就行了
//        //在创建 RpcServer 时需要传入一个已经注册好服务的 ServiceRegistry，原来的 register 方法也被改成了 start 方法
//        //创建服务端的时候，传入注册容器
//        NettyServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
//        //启动服务端 ，就会
//        server.publishService(helloService, HelloService.class);
//        /**
//         * 1.调用start，就会执行服务端，然后ServerSocket监控端口
//         * 2.当端口有信息来的时候，线程池创建执行线程，创建新的RequestHandlerThread（处理服务端接受到的服务的工作线程）
//         * 3.处理服务端接收到服务的 工作线程，有三个参数1.socket--传入的request对象，2.requestHandler-实际执行方法调用的处
//         * 理器3.serviceRegistry-以及注册好服务的注册表
//         * 4.然后有了新的工作线程，因为工作线程实现runnable接口，所以生成线程的时候回自动启动run方法
//         * 5.run方法里面：
//         * ①会根据socket给的然后解析出request对象
//         * ②然后会获得request的接口的接口名，通过接口名和注册表可以获取到服务对象
//         * ③然后传入服务对象，和request对象 向requestHandler中来处理服务获得返回的结果
//         * ④写入socket,客户端就能看到了
//         */
//
//
//        server.publishService(helloService, HelloService.class);
    }
}
