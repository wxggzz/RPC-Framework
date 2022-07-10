package com.panda.rpc.netty.client;

import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.RpcResponse;
import com.panda.rpc.entity.factory.SingletonFactory;
import com.panda.rpc.serializer.CommonSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/02/23:44
 * @Description: 客户端Netty处理器
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse>  {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);


    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler(){
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            //客户端收到的方法回调结果的消息
            logger.info(String.format("客户端接收到消息：%s", msg));
//            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + msg.getRequestId());
//            ctx.channel().attr(key).set(msg);
//            //关闭客户端通道
//            ctx.channel().close();
            //0608
            //将响应数据取出
            unprocessedRequests.complete(msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用中有错误发生：");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.WRITER_IDLE){
                logger.info("发送心跳包[{}]", ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.getChannel((InetSocketAddress) ctx.channel().remoteAddress(),
                        CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER));
                RpcRequest rpcRequest = new RpcRequest();
                //默认是false, 触发之后,设置为true
                //设置是心跳包
                rpcRequest.setHeartBeat(true);
                //设置一个Listener监测服务端是否接收到心跳包，如果接收到就代表对方在线，不用关闭Channel
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
