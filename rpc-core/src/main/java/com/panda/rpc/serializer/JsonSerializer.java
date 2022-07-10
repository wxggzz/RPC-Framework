package com.panda.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/02/17:45
 * @Description:使用Json格式的序列化器
 *
 */
public class JsonSerializer  implements  CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    /**
     *
     * 序列化和反序列化，就是把对象转换成字节数组，和根据字节数组和 Class 反序列化成对象
     * @param obj 传入的对象
     * @return
     */
    @Override
    public byte[] serialize(Object obj) {
        try{
            return objectMapper.writeValueAsBytes(obj);
        }catch (JsonProcessingException e){
            logger.error("序列化时有错误发生：{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * 序列化和反序列化，就是把对象转换成字节数组，和根据字节数组和 Class 反序列化成对象
     * @param bytes 传入的字节数组
     * @param clazz
     * @return
     */
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try{
            Object obj = objectMapper.readValue(bytes, clazz);
            if(obj instanceof RpcRequest){
                obj = handleRequest(obj);
            }
            return obj;
        }catch (IOException e){
            logger.error("反序列化时有错误发生：{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    /**
     *
     * //
     * @description 由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类，需要重新判断处理
     * 因为都是Object类型，分不清，所以得重新根据getParamTypes来判断实际的实例对象
     * @param obj
     * @return [java.lang.Object]
     * @date [2021-02-22 15:03]
     */
    private Object handleRequest(Object obj) throws IOException{
        RpcRequest rpcRequest = (RpcRequest) obj;
        for(int i = 0; i < rpcRequest.getParamTypes().length; i++){
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())){
                logger.info("JSON反序列化后错误，进行修改："+clazz.toString()+"与" +rpcRequest.getParameters()[i].getClass().toString());
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, clazz);

            }
        }
        return rpcRequest;
    }
    //上面提到的这种情况不会在其他序列化方式中出现，因为其他序列化方式是转换成****字节数组***，会记录对象的信息，
    //而 JSON 方式本质上只是转换成 JSON 字符串，会丢失对象的类型信息。
    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
