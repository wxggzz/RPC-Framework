package com.panda.rpc.socket.server;

import com.panda.rpc.RequestHandler;
import com.panda.rpc.RpcServer;
import com.panda.rpc.entity.enumeration.RpcError;
import com.panda.rpc.entity.exception.RpcException;
import com.panda.rpc.hook.ShutdownHook;
import com.panda.rpc.provider.ServiceProvider;
import com.panda.rpc.provider.ServiceProviderImpl;
import com.panda.rpc.register.NacosServiceRegistry;
import com.panda.rpc.register.ServiceRegistry;
import com.panda.rpc.serializer.CommonSerializer;
import com.panda.rpc.entity.factory.ThreadPoolFactory;
import com.panda.rpc.transport.AbstractRpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer extends AbstractRpcServer {
    //在创建 RpcServer 时需要传入一个已经注册好服务的 ServiceRegistry

    private final ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    private CommonSerializer serializer;


    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializerCode){
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        serializer = CommonSerializer.getByCode(serializerCode);
        //创建线程池
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        //自动注册服务
        scanServices();
    }

//    //要有服务注册的方法
//    @Override
//    public <T> void publishService(T service, Class<T> serviceClass) {
//        if (serializer == null){
//            logger.error("未设置序列化器");
//            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
//        }
//        serviceProvider.addServiceProvider(service, serviceClass);
//        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
//        start();
//    }

    /**
     * @return [void]
     * @description 服务端启动
     * @date [2021-02-05 11:57]
     */
    @Override
    public void start(){
         try(ServerSocket serverSocket = new ServerSocket()){
                serverSocket.bind(new InetSocketAddress(host, port));
                logger.info("服务器启动……");
                //添加钩子，服务端关闭时会注销服务
                ShutdownHook.getShutdownHook().addClearAllHook();
                Socket socket;
                //当未接收到连接请求时，accept()会一直阻塞
                while ((socket = serverSocket.accept()) != null) {
                    logger.info("客户端连接！IP：" + socket.getInetAddress());
                    //然后交给处理请求的工作线程RequestHandlerThread，它主要是利用客户端传来的RpcRequest对象，处理器，从ServiceRegistry 中获取提供服务的对象。
                    //如果收到消息的话，线程池就会创建一个新线程，
                    // 第一个参数：传入socket,
                    // 第二个参数：实际执行方法调用的处理器（执行handle就会处理rpcRequest和service服务的处理请求，就调用需要执行服务的方法，然后返回返回值）
                    // 第三个参数：serviceRegistry，已经注册好服务的 ServiceRegistry注册表
                    threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer));
                    //在RequestHandler中再利用反射原理实现方法的调用处理，最后将结果返回给客户端。
                }
            } catch (IOException e) {
                logger.info("服务器启动时有错误发生：" + e);
            }
    }


}
