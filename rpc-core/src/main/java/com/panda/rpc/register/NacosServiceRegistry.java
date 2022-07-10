package com.panda.rpc.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.panda.rpc.entity.enumeration.RpcError;
import com.panda.rpc.entity.exception.RpcException;
import com.panda.rpc.entity.util.NacosUtil;
import io.protostuff.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/06/18:08
 * @Description:
 */
public class NacosServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

//    private static final String SERVER_ADDR = "127.0.0.1:8848";
//    private static final NamingService namingService ;
    //0606
    public final NamingService namingService;
    //初始化namingService，连接nacos，创建命名服务
    //0613 实现均衡算法

//    static {
//        try {
//            //连接Nacos创建命名服务
//            namingService = NamingFactory.createNamingService(SERVER_ADDR);
//        }catch (NacosException e){
//            logger.error("连接Nacos时有错误发生：" + e);
//            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
//        }
//    }
        //原本是直接调用方法，现在封装了一下
        public NacosServiceRegistry(){
            namingService = NacosUtil.getNacosNamingService();
        }


    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            //提供，服务名称,host,port，向nacos注册服务
            NacosUtil.registerService(serviceName, inetSocketAddress);

        } catch (NacosException e) {
            logger.error("注册服务时有错误发生" + e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }

    }
}
