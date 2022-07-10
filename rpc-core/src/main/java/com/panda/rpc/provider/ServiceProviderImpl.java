package com.panda.rpc.provider;


import com.panda.rpc.entity.enumeration.RpcError;
import com.panda.rpc.entity.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/05/27/18:14
 * @Description:
 */
public class ServiceProviderImpl implements ServiceProvider {
    //新建日志对象
    private static final Logger logger =  LoggerFactory.getLogger(ServiceProviderImpl.class);
    //使用map来获取对象服务，使用set来判断是否存在
    /**
     * 存放服务接口名称，和服务
     * key用来存放接口名 和 value=实现类的对象  一个接口可以有多个实现类
     * 使用接口名称来获取实现这个接口的服务
     */
    private static final Map<String,Object> serviceMap =  new ConcurrentHashMap<>();
    /**
     * 用来存在服务，
     * 用来存在实现类的名称，一个实现类就相当于一个服务，存放实现类是因为一个实现类可能会实现多个接口，省空间
     */
    private static final Set<String> registeredService =  ConcurrentHashMap.newKeySet();

    /**
     *
     * @param service 待注册的服务实体
     * @param <T>
     *     synchronized 为了保证同时只有一个线程进行注册操作
     *     1.首先判断是否已经注册过这个服务了，使用set-registeredService判断 ，注册过的都会放入set里面
     *     2.其次再获得当前服务的所有接口
     *     3.然后把所有把当前服务的接口都存入map
     */
    @Override
//    public <T> void addServiceProvider(T service) {
//        //会获得com.panda.rpc.impl  就是service的全路径名称
//        String name = service.getClass().getCanonicalName();
//        //如果已经注册过了，就不用注册了
//        if(registeredService.contains(name)){
    public <T> void addServiceProvider(T service, String serviceName) {
        if(registeredService.contains(serviceName)){
            return;
        }
        logger.info("name：" +  service.getClass().getCanonicalName());
        //注册进set
//        registeredService.add(name);
//        //获得当前对象的所有接口，放入map中，可以获得注册服务和接口的关系
//        logger.info("interfaces：" + service.getClass().getInterfaces());
//        Class<?>[] interfaces = service.getClass().getInterfaces();
//        if(interfaces.length==0){
//            return;
//        }
//        //每个接口只会对应一个对象，不会出现一个接口有好多个对象的情况，key相同的情况下，value肯定不相同
//        for (Class<?> anInterface : interfaces) {
//            logger.info("anInterface"+anInterface);
//            logger.info("anInterface.getCanonicalName()："+anInterface.getCanonicalName());
//            //存放服务接口全路径名称，还有实现类对象
//            serviceMap.put(anInterface.getCanonicalName(),service);
//        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口：{} 注册服务：{}", service.getClass().getInterfaces(), serviceName);

    }

    /**
     * 根据服务的接口名称获取服务，map
     * @param serviceName 服务名称
     * @return
     */
    @Override
    public Object getServiceProvider(String serviceName) {
        Object o = serviceMap.get(serviceName);
        if(o == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return o;
    }
}
