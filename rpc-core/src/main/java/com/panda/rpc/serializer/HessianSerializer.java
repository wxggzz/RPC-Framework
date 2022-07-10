package com.panda.rpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.panda.rpc.entity.enumeration.SerializerCode;
import com.panda.rpc.entity.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/05/9:21
 * @Description: 基于Hessian的序列化器
 * 1.也是先创个流，然后放入output input
 * 2.也是使用writeObject 和readObject
 */
public class HessianSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);
    @Override
    public byte[] serialize(Object obj) throws IOException {
        HessianOutput hessianOutput = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("序列化时发生错误："+e);
            throw new SerializeException("序列化时发生错误");
        } finally {
            if (hessianOutput==null){
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    logger.error("关闭hessian时发生错误："+e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try {
            //先将字节数组写入流中
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            hessianInput = new HessianInput(byteArrayInputStream);
            return hessianInput.readObject();
        } catch (IOException e) {
            logger.error("序列化时发生错误："+e);
            throw new SerializeException("序列化时发生错误");
        }finally {
            if (hessianInput==null){
                hessianInput.close();
            }
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("HESSIAN").getCode();
    }
}
