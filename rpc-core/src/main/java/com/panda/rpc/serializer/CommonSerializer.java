package com.panda.rpc.serializer;


import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/02/17:43
 * @Description:通用的序列化反序列化接口
 * 有函数：1序列化 2.反序列化， 3.获得序列化器的编码 4.根据编码获得序列化器
 */
public interface CommonSerializer {
    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;
    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    byte[] serialize(Object obj) throws IOException;

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();
    //引入KRYO,修改通用序列化器，添加kryo的情况
    //0605引入Hessian
    static CommonSerializer getByCode(int code){
        switch (code){
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new ProtostuffSerializer();
            default:
                return null;
        }
    }

}
