package com.panda.rpc.entity.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.panda.rpc.entity.enumeration.RpcError;
import com.panda.rpc.entity.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/06/21:55
 * @Description:
 */
public class NacosUtil {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);
    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;
    private static final String SERVER_ADDR = "127.0.0.1:8848";

    static {
        namingService = getNacosNamingService();
    }


    /**
     * @description 连接到Nacos创建命名空间
     * @return [com.alibaba.nacos.api.naming.NamingService]
     * @date [2021-03-14 9:33]
     */
    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        }catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生：", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    /**
     * @description 注册服务到Nacos
     * @param address, serviceName, inetSocketAddress]
     * @return [void]
     * @date [2021-03-14 9:34]
     */
    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtil.address = address;
        //保存注册的服务名
        serviceNames.add(serviceName);
    }

    /**
     * @description 获取所有提供该服务的服务端地址
     * @param serviceName, serviceName]
     * @return [java.util.List<com.alibaba.nacos.api.naming.pojo.Instance>]
     * @date [2021-03-14 9:39]
     */
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static  void clearRegistry(){
        //所有服务名称都被存储在serviceNames set中
        //如果不为空或者地址不为空则就可以清空
        if(!serviceNames.isEmpty() && address!=null){
            String hostName = address.getHostName();
            int port = address.getPort();
            //使用迭代器注销
            Iterator<String> iterator = serviceNames.iterator();
            while(iterator.hasNext()){
                String serviceName  = iterator.next();
                //调用nacos注销函数
                try {
                    namingService.deregisterInstance(serviceName,hostName, port);
                } catch (NacosException e) {
                    logger.error("注销服务{}失败", serviceName, e);
                }

            }
        }
    }
}