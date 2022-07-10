package com.panda.rpc.socket.util;

import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.RpcResponse;
import com.panda.rpc.entity.enumeration.PackageType;
import com.panda.rpc.entity.enumeration.RpcError;
import com.panda.rpc.entity.exception.RpcException;
import com.panda.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/05/16:55
 * @Description: Socket方式从输入流中读取字节并反序列化【解码】  编码和解码（序列化和反序列化）
 */
public class ObjectReader {
    private static final Logger logger = LoggerFactory.getLogger(ObjectReader.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static Object readObject(InputStream in) throws IOException {

        byte[] numberBytes = new byte[4];
        //读入四个字节，判断自定义协议中的标志是否正确
        in.read(numberBytes);
        int magic = bytesToInt(numberBytes);
        if (magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包：{}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        //读入四个字节，判断自定义协议中响应类型是否正确
        in.read(numberBytes);
        int packageCode = bytesToInt(numberBytes);
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包：{}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        //读入四个字节，判断自定义协议中序列化器是否正确
        in.read(numberBytes);
        int serializerCode = bytesToInt(numberBytes);
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.error("不识别的反序列化器：{}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        ////读入四个字节，判断自定义协议中数据长度
        in.read(numberBytes);
        int length = bytesToInt(numberBytes);
        byte[] bytes = new byte[length];
        in.read(bytes);
        //返回反序列化后的结果
        return serializer.deserialize(bytes, packageClass);
    }

    /**
     * @description 字节数组转换为Int
     * @param src
     * @return [int]
     * @date [2021-03-10 21:57]
     */
    private static int bytesToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF) << 24)
                |((src[1] & 0xFF) << 16)
                |((src[2] & 0xFF) << 8)
                |(src[3] & 0xFF);
        return value;
    }
}


