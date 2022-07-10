package com.panda.rpc.register;

import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/06/18:02
 * @Description:  服务注册接口
 */
public interface ServiceRegistry {

    /**
     * @description 将一个服务注册到注册表
     * @param serviceName, inetSocketAddress] 服务名称，提供服务的地址
     * @return [void]
     */
    void register(String serviceName , InetSocketAddress inetSocketAddress);

}
