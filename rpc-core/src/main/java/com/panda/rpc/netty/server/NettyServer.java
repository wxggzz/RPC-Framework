package com.panda.rpc.netty.server;

import com.panda.rpc.RpcServer;
import com.panda.rpc.codec.CommonDecoder;
import com.panda.rpc.codec.CommonEncoder;
import com.panda.rpc.entity.enumeration.RpcError;
import com.panda.rpc.entity.exception.RpcException;
import com.panda.rpc.hook.ShutdownHook;
import com.panda.rpc.provider.ServiceProvider;
import com.panda.rpc.provider.ServiceProviderImpl;
import com.panda.rpc.register.NacosServiceRegistry;
import com.panda.rpc.register.ServiceRegistry;
import com.panda.rpc.serializer.CommonSerializer;
import com.panda.rpc.serializer.HessianSerializer;
import com.panda.rpc.serializer.JsonSerializer;
import com.panda.rpc.serializer.KryoSerializer;
import com.panda.rpc.transport.AbstractRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/02/0:42
 * @Description:
 */
public class NettyServer extends AbstractRpcServer {

    private CommonSerializer serializer;
//    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
//
//
//    private final String host;
//    private final int port;
//
//    private final ServiceRegistry serviceRegistry;
//    private final ServiceProvider serviceProvider;
    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }
    public NettyServer(String host, int port, Integer serializerCode) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        serializer = CommonSerializer.getByCode(serializerCode);
        //初始化netty服务端的时候就自动注册服务,使用注解
        scanServices();
    }


//    @Override
//    public <T> void publishService(T service, Class<T> serviceClass) {
//        if (serializer == null) {
//            logger.error("未设置序列化器");
//            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
//        }
//        serviceProvider.addServiceProvider(service, serviceClass);
//        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
//        start();
//    }

    //Netty处理服务器的开始的配置
    @Override
    public void start() {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //首先配置两个线程组，一个来处理连接，一个处理工作逻辑.
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端入口，初始化服务端启动器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //进行信息配置
            //将两个线程组初始化到启动器中
            serverBootstrap.group(bossGroup,workerGroup)
                    //设置服务端通道的类型
                    .channel(NioServerSocketChannel.class)
                    //日志打印的方式
                    //通过handler添加的handlers是对bossGroup线程组起作用
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //配置ServerChannel参数，服务端接受连接的最大队列长度，如果队列已满，客户端连接将被拒绝
                    //backlog对程序的连接数没影响，但是影响的是还没有被Accept取出的连接。
                    .option(ChannelOption.SO_BACKLOG, 256)
                    //类似于心跳机制，固定时间，探测空闲时间的有效性 7200s
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    //配置Channel参数，nodelay没有延迟，true就代表禁用Nagle算法，减小传输延迟
                    .option(ChannelOption.TCP_NODELAY,true)
                    //最后初始化handler，设置操作.配置子channel的handler
                    ////通过childHandler添加的handlers是对workerGroup线程组起作
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //每一个channel都会有一个管道,初始化管道
                            ChannelPipeline pipeline = ch.pipeline();
                            //设定IdleStateHandler心跳检测每30秒进行一次读检测，如果30秒内ChannelRead()方法未被调用则触发一次userEventTrigger()方法
                            //第一个参数就是读检测的意思，
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                            //就是添加要处理的逻辑，编码解码然后获得结果
                            //执行handler往管道中添加Handler，注意入站Handler与出站Handler都必须按实际执行顺序添加，
                            //修改new HessianSerializer()为serializer，使得可以在初始化客户端服务端的时候就可以指定序列器
                                    .addLast(new CommonEncoder(serializer))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());

                        }
                    });
            //开始绑定端口，启动netty,sync()代表阻塞主Server线程，以执行Netty线程，如果不阻塞Netty就直接被下面shutdown了
            //shutdownGracefully();
            ChannelFuture future = serverBootstrap.bind(host,port).sync();
            //添加注销服务的钩子，服务端关闭时才会执行
            ShutdownHook.getShutdownHook().addClearAllHook();
            //确定通道关闭了，关闭future 回到主Server线程
            //一定要关闭future
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生", e);
        }finally {
            //优雅关闭Netty服务端且清理掉内存，shutdownGracefully()执行逻辑参考：https://www.icode9.com/content-4-797057.html
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }



}
