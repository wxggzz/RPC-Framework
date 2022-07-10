package com.panda.rpc.netty.client;

import com.panda.rpc.codec.CommonDecoder;
import com.panda.rpc.codec.CommonEncoder;
import com.panda.rpc.entity.enumeration.RpcError;
import com.panda.rpc.entity.exception.RpcException;
import com.panda.rpc.serializer.CommonSerializer;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.Bootstrap;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/05/20:09
 * @Description: 用于获取Channel对象
 *
 *  */
public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    //客户端只需要一个eventLoopGroup,进行初始化
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();
    //设置重连次数
    private static final int   MAX_RETRY_COUNT = 5;
    //先创建一个空channel
    private static Channel channel;

    /**
     * 所有客户端Channel都保存在该Map中
     */
    private static Map<String, Channel> channels = new ConcurrentHashMap<>();
    //初始化BootStrap ,返回一个设置线程组，设置channel还有设置属性的bootstrap
    private static Bootstrap initializeBootstrap(){
        //1.创建线程组
        eventLoopGroup = new NioEventLoopGroup();
        //2.创建客户端启动接口
        Bootstrap bootstrap = new Bootstrap();
        //一般都是1.先设置线程组，2.设置channel类型3.设置属性keepalive这种的
        bootstrap.group(eventLoopGroup)
                //设置channel类型
                .channel(NioSocketChannel.class)
                //设置连接的超时时间，超过这个时间就代表连接失败了
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                //相当于心跳机制，默认7200s,tcp会主动探测空闲连接的有效性
                .option(ChannelOption.SO_KEEPALIVE, true)
                //配置Channel参数，nodelay没有延迟，true就代表禁用Nagle算法，减小传输延迟。
                .option(ChannelOption.TCP_NODELAY,false);
        return bootstrap;
    }

    /**
     *
     * @param inetSocketAddress 传输地址port
     * @param serializer 序列器
     * @return 通道channel
     */
    public static Channel getChannel(InetSocketAddress inetSocketAddress, CommonSerializer serializer){
        //先判断是否包含channel,如果包含并且还active中，则直接return获取，如果不包含，移除这个key后面再获取
        String key = inetSocketAddress.toString() + serializer.getCode();
        if(channels.containsKey(key)){
            Channel channel = channels.get(key);
            if(channel != null && channel.isActive()){
                return channel;
            }else {
                channels.remove(key);
            }
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        //设定IdleStateHandler心跳检测每5秒进行一次写检测，如果5秒内write()方法未被调用则触发一次userEventTrigger()方法
                        //实现客户端每5秒向服务端发送一次消息
                        //客户端超过5s未写数据，触发写事件，向服务端发送心跳包，
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });

        //CountDownLatch接收一个int型参数，表示要等待的工作线程的个数。
        //等待的工作线程个数为1
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        try {
//            //阻塞当前线程直到计时器的值为0 ,就是CountDownLatch为0
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            logger.error("获取Channel时有错误发生", e);
//        }
        try {
            channel = connect(bootstrap, inetSocketAddress);
    }catch (ExecutionException | InterruptedException e){
        logger.error("连接客户端时有错误发生", e);
        return null;
    }
        channels.put(key, channel);
        return channel;
    }

    //     * @description Netty客户端创建通道连接
    //     * @param [bootstrap, inetSocketAddress]
    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws
            ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();        //boostrap开始连接,inetSocketAddress里面包括ip和端口，并且添加监控器
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener)future ->{
            //判断是否成功，异常判断
            if(future.isSuccess()){
                logger.info("客户端连接成功啦！嘿嘿");
                completableFuture.complete(future.channel());
            }else {
                throw new IllegalStateException();
            }
            //计算重连间隔时间打印出来
//            //第几次重连
//            int order = (MAX_RETRY_COUNT -retry)+1;
//            //重连的时间间隔，相当于1乘以2的order次方
//            int delay = 1<<order;
//            //如果连接失败但是重试次数仍然没有用完，则计算下一次重连间隔 delay，然后定期重连
//            //我们定时任务是调用 bootstrap.config().group().schedule(), 其中 bootstrap.config()
//            // 这个方法返回的是 BootstrapConfig，他是对 Bootstrap 配置参数的抽象，
//            // 然后 bootstrap.config().group() 返回的就是我们在一开始的时候配置的线程模型 eventLoopGroup，
//            // 调 eventLoopGroup 的 schedule 方法即可实现定时任务逻辑
//            //就是设置定时重连，时间为delay
//            bootstrap.config().group().schedule(()-> connect(bootstrap,inetSocketAddress,retry-1,countDownLatch)
//                    ,delay, TimeUnit.SECONDS);
        });
            return completableFuture.get();
    }

}
