package com.panda.rpc.socket.util;

import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.enumeration.PackageType;
import com.panda.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/05/17:00
 * @Description:
 */
public class ObjectWriter {

    private static final Logger logger = LoggerFactory.getLogger(ObjectWriter.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static void writeObject(OutputStream out, Object object, CommonSerializer serializer) throws IOException {
        out.write(intToBytes(MAGIC_NUMBER));
        if(object instanceof RpcRequest) {
            out.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        } else {
            out.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }
        out.write(intToBytes(serializer.getCode()));
        byte[] bytes = serializer.serialize(object);
        out.write(intToBytes(bytes.length));
        out.write(bytes);
        out.flush();
    }

    /**
     * @description 将Int转换为字节数组
     * @param value
     * @return [byte[]]
     * @date [2021-03-10 22:15]
     */
    private static byte[] intToBytes(int value) {

        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16) & 0xFF);
        src[2] = (byte) ((value>>8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}