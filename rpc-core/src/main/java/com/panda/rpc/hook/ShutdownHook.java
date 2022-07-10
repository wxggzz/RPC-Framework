package com.panda.rpc.hook;

import com.panda.rpc.entity.factory.ThreadPoolFactory;
import com.panda.rpc.entity.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/07/16:30
 * @Description:
 */
public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");

    /**
     *
     * 使用单例模式创建钩子，保证全局只有这一个钩子
     */
    private static final ShutdownHook shutdownHook  = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    //注销服务的钩子
    public  void addClearAllHook(){
        logger.info("服务端关闭前将自动注销所有服务");
        //当jvm关闭的时候，会执行系统中已经设置的所有通过方法addShutdownHoo添加的内容
        //Runtime对象是JVM虚拟机的运行时环境，增加一个钩子函数，创建一个新线程调用clearRegistry()完成注销工作,
        //这个钩子函数会在JVM关闭之前调用
        //这样只需要把钩子放在服务端，启动服务端时就能注册钩子了，以NettyServer为例，启动服务端后再关闭
        //启动的时候加入这个钩子
        Runtime.getRuntime().addShutdownHook(new Thread(()->    {
            NacosUtil.clearRegistry();
            //注销服务业同时关闭所有线程池
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
