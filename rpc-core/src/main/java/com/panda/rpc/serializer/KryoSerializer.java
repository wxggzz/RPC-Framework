package com.panda.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.RpcResponse;
import com.panda.rpc.entity.enumeration.ResponseCode;
import com.panda.rpc.entity.enumeration.SerializerCode;
import com.panda.rpc.entity.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.krb5.internal.KRBCred;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/04/21:49
 * @Description:
 */
public class KryoSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    //使用ThreadLocal的Kryo对象能保证线程安全

    //将kryo对象存储在线程中，只有这个线程可以访问到，这样保证kryo的线程安全性，ThreadLocal(线程内部存储类)
    //通过get()&set()方法读取线程内的数据
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()-> {
        Kryo kryo = new Kryo();
        //注册传送内容的两个类
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        //支持对象循环引用（否则会栈溢出）
        kryo.setReferences(true); //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置
        //不强制要求注册类，默认为false，若设置为true则要求涉及到的所有类都要注册，包括jdk中的比如Object
        kryo.setRegistrationRequired(false);
        return kryo;
    });



    //输出output写入,输入input读取
    /**
     * 进行序列化，将对象转换为字节数组
     * Kryo通常完成字节数组和对象的转换，所以常用的输入输出流实现为ByteArrayInputStream/ByteArrayOutputStream
     * @param obj
     * @return byte数组
     */
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream)){
        //创建线程安全的kryoThreadLocal
        Kryo kryo = kryoThreadLocal.get();
        kryo.writeObject(output,obj);
        //读完之后记得remove
        //方法删除此线程局部变量的当前线程值。
        kryoThreadLocal.remove();
        return output.toBytes();
        }catch (Exception e){
            logger.error("序列化时发生错误："+e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    /**
     * 进行反序列化，将byte数组转换成对象
     * @param bytes
     * @param clazz
     * @return 对象
     */
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream)){
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        }catch (Exception e){
            logger.error("反序列化时有错误发生：" + e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}

