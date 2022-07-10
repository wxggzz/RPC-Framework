package com.panda.rpc.socket.server;

import com.panda.rpc.RequestHandler;
import com.panda.rpc.entity.RpcRequest;
import com.panda.rpc.serializer.CommonSerializer;
import com.panda.rpc.socket.util.ObjectReader;
import com.panda.rpc.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/05/27/20:09
 * @Description: IO传输模式|处理客户端RpcRequest的工作线程
 */
public class SocketRequestHandlerThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
//        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
//            RpcRequest rpcRequest = (RpcRequest)objectInputStream.readObject();
        //0605引入自定义的写入，使得socket传输可以使用上不用的序列器
        try(InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
//            String interfaceName = rpcRequest.getInterfaceName();
//            Object service = serviceProvider.getServiceProvider(interfaceName);
//            //服务对象。根据服务对象获取方法的信息，然后执行方法，生成返回值
//            Object response = requestHandler.handle(rpcRequest, service);
            //0606
            Object response = requestHandler.handle(rpcRequest);
            ObjectWriter.writeObject(outputStream, response, serializer);
        }catch (IOException e){
            logger.info("调用或发送时发生错误：" + e);
        }
    }
}
