package com.panda.test;

import com.panda.rpc.RpcClient;
import com.panda.rpc.RpcClientProxy;
import com.panda.rpc.api.ByeService;
import com.panda.rpc.api.HelloObject;
import com.panda.rpc.api.HelloService;
import com.panda.rpc.loadbalancer.RoundRobinLoadBalancer;
import com.panda.rpc.netty.client.NettyClient;
import com.panda.rpc.serializer.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/03/0:05
 * @Description: netty客户端启动
 */
public class NettyTestClient {
    public static void main(String[] args) {
        /**
         * 1.先创建netty客户端
         * 2.创建代理中介,传入客户端
         * RpcClientProxy 通过传入不同的Client（SocketClient、NettyClient）来切换客户端不同的传输方式。
         *  也可以传入socket的
         */
        //nacos地址：http://127.0.0.1:8848/nacos/index.html#/
        //构建RpcClient 不需要传入host 和 port
        //因为现在这个host和port是从Nacos服务注册中心中获取的，
        //会自己寻找服务
        //第一个参数，为序列器，第二个参数为，轮转算法,按顺序来，就是负载均衡的实现

        RpcClient client = new NettyClient(CommonSerializer.KRYO_SERIALIZER, new RoundRobinLoadBalancer());
        //0605 修改了序列器的使用，不是调整内部代码。而是启动的时候指定
        RpcClientProxy proxy = new RpcClientProxy(client);
        //下面代码就是生成了接口的代理对象
        //代理对象可以拦截对原对象方法的调用并进行增强就行
        //这个对象的invoke()方法会在代理对象调用方法时触发，就相当于大家都说的“增强”
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(99, "this is a  netty message0606");
        String res = helloService.hello(object);
        System.out.println(res);
        //0614
        ByeService byeService = proxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
    }
}
