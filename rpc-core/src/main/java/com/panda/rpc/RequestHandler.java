package com.panda.rpc;

import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.RpcResponse;
import com.panda.rpc.entity.enumeration.ResponseCode;
import com.panda.rpc.provider.ServiceProvider;
import com.panda.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wxg
 * @date [2021-02-05 12:13]
 * @description 实际执行方法调用的处理器
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;
    static {
        serviceProvider = new ServiceProviderImpl();
    }
    public Object handle(RpcRequest rpcRequest){

        //从服务端本地注册表中获取服务实体
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest, service);


    }
    //获得执行服务的方法的返回值
    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) {
        Object result;
        try{
            //getClass()获取的是实例对象的类型
            //利用反射原理找到远程所需调用的方法
            //该方法的第一个参数name是要获得方法的名字，第二个参数parameterTypes是按声明顺序标识该方法形参类型。
            //比如获取getMethod("hello", rpcRequest.getParamTypes()
            //就是获得service的hello方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务：{}成功调用方法：{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        //invoke(obj实例对象,obj可变参数)
        //用来执行某个对象的目标方法
        //方法来反射调用一个方法，当然一般只用于正常情况下无法直接访问的方法
        //然后将方法的返回值写入socket传回去，就是将调用hello方法的返回值写入socket，然后客户端接收到
        //method.invoke方法，传入对象实例和参数，即可调用并且获得返回值。
        //传入参数调用方法
        //方法调用成功
        return RpcResponse.success(result, rpcRequest.getRequestId());
    }
}
