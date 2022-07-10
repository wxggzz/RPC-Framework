package com.panda.rpc.codec;

import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.enumeration.PackageType;
import com.panda.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/02/22:53
 * @Description: 通用编码拦截器 ,编码器，将数据对象转换成字节数组
 */
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private static final Logger logger = LoggerFactory.getLogger(CommonEncoder.class);
    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer){
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //数据写到缓冲区
        //1.先写入自定义的协议标识，用来标识我们自定义的协议
        out.writeInt(MAGIC_NUMBER);
        //2.写入请求类型，是调用请求还是响应结果
        if(msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        //3.写入该序列化器的编号，

        int code = serializer.getCode();
        logger.info("调用"+code+"号序列器！" + serializer.toString());
        out.writeInt(code);
        //4.进行序列化 得到字节数组，长度和数据写入out里面
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}