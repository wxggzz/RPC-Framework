package com.panda.rpc;

import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.RpcResponse;
import com.panda.rpc.entity.util.RpcMessageChecker;
import com.panda.rpc.netty.client.NettyClient;
import com.panda.rpc.socket.client.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 使用动态代理生成接口的实现类
 *
 * getProxy中介对象，生成代理对象
 * 然后每次调用方法的时候，都会调用invoke，所以我们在invoke里面发送RpcRequest
 */

public class RpcClientProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    private final RpcClient client;
    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }


    //传入的是真实对象
    //抑制警告信息
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> realEntity){
        //使用Proxy 生成代理对象
        /**
         * 1.第一个参数是真实对象的类加载器
         * 2.第二个
         */
        return (T) Proxy.newProxyInstance(realEntity.getClassLoader(),new Class<?>[]{realEntity},this);
    }
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)  {
        logger.info("调用方法：{}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), false);
        //return client.sendRequest(rpcRequest);
        //原本是直接获得结果，现在是异步获得
        RpcResponse rpcResponse = null;
        if(client instanceof NettyClient){

            try {
                //异步获取调用结果
                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>)client.sendRequest(rpcRequest);
                //result= completableFuture.get().getData();
                rpcResponse = completableFuture.get();
            }catch (Exception e){
                logger.error("方法调用请求发送失败", e);
                return null;
            }
        }
        if(client instanceof SocketClient){
//            RpcResponse rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);
//            result = rpcResponse.getData();
            rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);

        }
        //获得服务端返回的结果之前，都进行检查
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }
}
