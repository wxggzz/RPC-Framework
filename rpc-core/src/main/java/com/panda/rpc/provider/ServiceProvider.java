package com.panda.rpc.provider;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/05/27/18:17
 * @Description:
 */
public interface ServiceProvider {
    /**
     * @description 将一个服务注册进注册表
     * @param service 待注册的服务实体
     * @return [void]
     * @date [2021-02-07 16:59]
     */
    <T> void addServiceProvider(T service, String serviceName);

    /**
     * @description 根据服务名获取服务实体
     * @param serviceName 服务名称
     * @return [java.lang.Object] 服务实体
     * @date [2021-02-07 17:06]
     */
    Object getServiceProvider(String serviceName);
}
