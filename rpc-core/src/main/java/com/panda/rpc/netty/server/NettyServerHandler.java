package com.panda.rpc.netty.server;

import com.panda.rpc.RequestHandler;
import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.factory.ThreadPoolFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/02/23:24
 * @Description:
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private final RequestHandler requestHandler;

    public NettyServerHandler(){
        requestHandler = new RequestHandler();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.READER_IDLE){
                logger.info("长时间未收到心跳包，断开连接……");
                ctx.close();
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
//        try{
//            //msg:RpcRequest(interfaceName=com.panda.rpc.api.HelloService, methodName=hello,
//            //parameters=[HelloObject(id=99, message=this is a message0603)], paramTypes=[class com.panda.rpc.api.HelloObject])
//            logger.info("服务端接收到请求：{}", msg);
//            //获取接口名
//            String interfaceName = msg.getInterfaceName();
//            //根据接口名获取服务实例对象
//            Object service = serviceProvider.getServiceProvider(interfaceName);
//            //获取消息和服务，执行根据反射执行方法调用。返回结果
//            Object response = requestHandler.handle(msg, service);
//            //写入
//            ChannelFuture future = ctx.writeAndFlush(response);
//            //添加一个监听器到channelfuture来检测是否所有的数据包都发出，然后关闭通道
//            future.addListener(ChannelFutureListener.CLOSE);
//        }finally {
//            ReferenceCountUtil.release(msg);
//        }
        //0606更新
//        threadPool.execute(() -> {
//            try{
//                logger.info("服务端接收到请求：{}", msg);
//                Object response = requestHandler.handle(msg);
//                //注意这里的通道是workGroup中的，而NettyServer中创建的是bossGroup的，不要混淆
//                ChannelFuture future = ctx.writeAndFlush(response);
//                //添加一个监听器到channelfuture来检测是否所有的数据包都发出，然后关闭通道
//                //当操作失败或者被取消,了就关闭通道
//                future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
//            }finally {
//                ReferenceCountUtil.release(msg);
        //0608g更新心跳包
        try{
            //如果为true则说明接受到客户端更改属性，则说明客户端发送了心跳包
            if(msg.getHeartBeat()){
                logger.info("接收到客户端心跳包……");
                return;
            }
            logger.info("服务端接收到请求：{}", msg);
            Object response = requestHandler.handle(msg);
            //注意这里的通道是workGroup中的，而NettyServer中创建的是bossGroup的，不要混淆
            ChannelFuture future = ctx.writeAndFlush(response);
            //当操作失败或者被取消了就关闭通道
            future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            //0608
//            if(ctx.channel().isActive() && ctx.channel().isWritable()) {
//                //注意这里的通道是workGroup中的，而NettyServer中创建的是bossGroup的，不要混淆
//                ctx.writeAndFlush(response);
//            }else {
//                logger.error("通道不可写");
//            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生：");
        cause.printStackTrace();
        ctx.close();
    }
}
