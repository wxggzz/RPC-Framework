package com.panda.rpc.entity.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/07/16:10
 * @Description: 单例工厂，保证一个类只有一个实例，节约资源，保证线程安全
 */
public class SingletonFactory {
    //放置单例的map实例
    private static Map<Class , Object> objectMap = new HashMap<>();

    //单例模式构造方法是私有的
    private SingletonFactory(){}

    //获取实例对象，单例对象必须由单例类自行创建；
    public static <T> T getInstance(Class<T> clazz){
        Object instance = objectMap.get(clazz);
        //锁住保证线程安全
        synchronized (clazz){
            //判断实例是否为空
            if(instance == null){
                try {
                    instance = clazz.newInstance();
                    //放入map
                    objectMap.put(clazz,instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e.getMessage(), e);

                }
            }
        }
        return clazz.cast(instance);
    }


}
