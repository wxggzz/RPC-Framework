package com.panda.rpc.codec;

import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.RpcResponse;
import com.panda.rpc.entity.enumeration.PackageType;
import com.panda.rpc.entity.enumeration.RpcError;
import com.panda.rpc.entity.exception.RpcException;
import com.panda.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/02/22:44
 * @Description: 解码器
 * 同时编码器使用writeInt WriteByte  则解码器也是按顺序读取
 */
public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        //从缓冲区中读取数据
        int magic = in.readInt();
        //如果不是我们自定义的协议，则是不识别的协议包
        if(magic != MAGIC_NUMBER){
            logger.error("不识别的协议包：{}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        //获取相应代码，0的话发送请求，1：响应结果，其他不能响应，获取响应的返回格式的class
        int packageCode = in.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if (packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else {
            logger.error("不识别的数据包：{}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        //获取序列器的编码
        int serializerCode = in.readInt();
        //根据编码获取序列器
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null){
            logger.error("不识别的反序列化器：{}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        //获取长度
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        //传入数据和类型 ，进行反序列得到对象
        Object obj = serializer.deserialize(bytes, packageClass);
        //添加到对象列表
        out.add(obj);
    }
}
