package com.panda.rpc.socket.client;

import com.panda.rpc.RpcClient;
import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.entity.RpcResponse;
import com.panda.rpc.entity.enumeration.ResponseCode;
import com.panda.rpc.entity.enumeration.RpcError;
import com.panda.rpc.entity.exception.RpcException;
import com.panda.rpc.entity.util.RpcMessageChecker;
import com.panda.rpc.loadbalancer.LoadBalancer;
import com.panda.rpc.loadbalancer.RandomLoadBalancer;
import com.panda.rpc.register.NacosServiceDiscovery;
import com.panda.rpc.register.ServiceDiscovery;
import com.panda.rpc.serializer.CommonSerializer;
import com.panda.rpc.socket.util.ObjectReader;
import com.panda.rpc.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
/**
 * @author [wxg]
 * @date [2022-06-01 17:20]
 * @description Socket方式进行远程调用的客户端
 */
public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    //发送rpcRequest对象，ip,port ,然后data

    /**
     *
     * 1.使用socket来发送数据
     * 2.ObjectOutputStream是发送数据过去
     * 3.ObjectInputStream是接受数据
     * @param rpcRequest
     * @param host
     * @param port
     * @return
     */

    private final CommonSerializer serializer;

    private final ServiceDiscovery serviceDiscovery;

    public SocketClient() {
        //以默认序列化器调用构造函数
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public SocketClient(LoadBalancer loadBalancer){
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public SocketClient(Integer serializerCode){
        this(serializerCode, new RandomLoadBalancer());
    }

    public SocketClient(Integer serializerCode, LoadBalancer loadBalancer) {
        serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        serializer = CommonSerializer.getByCode(serializerCode);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest){
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //从Nacos获取提供对应服务的服务端地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        /**
         * socket套接字实现TCP网络传输
         * try()中一般放对资源的申请，若{}出现异常，()资源会自动关闭
         */
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse = (RpcResponse) obj;
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse;
        } catch (IOException e) {
            logger.error("调用时有错误发生：" + e);
            throw new RpcException("服务调用失败：", e);
        }
    }

}
