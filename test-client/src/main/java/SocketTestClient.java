import com.panda.rpc.RpcClientProxy;
import com.panda.rpc.api.ByeService;
import com.panda.rpc.api.HelloObject;
import com.panda.rpc.api.HelloService;
import com.panda.rpc.loadbalancer.RoundRobinLoadBalancer;
import com.panda.rpc.serializer.CommonSerializer;
import com.panda.rpc.serializer.HessianSerializer;
import com.panda.rpc.serializer.ProtostuffSerializer;
import com.panda.rpc.socket.client.SocketClient;

public class SocketTestClient {
//    public static void main(String[] args) {
//        //接口与代理对象之间的中介对象
//        com.panda.rpc.RpcClientProxy proxy = new com.panda.rpc.RpcClientProxy("127.0.0.1", 9000);
//        //创建代理对象
//        HelloService helloService = proxy.getProxy(HelloService.class);
//        //接口方法的参数对象
//        HelloObject object = new HelloObject(12, "This is test message");
//        //由动态代理可知，代理对象调用hello()实际会执行invoke()，用动态代理获得的代理对象，然后执行方法
//        //执行方法会执行invoke()。invoke里面1.会先用builder()模式创建Request对象2.然后客户端会调用发送sendRequest函数
//        //3.sendRequest 发送 request对象， host ， 还有port
//        //4.sendRequest 1.里面使用port 和host 创建socket对象，2.使用socket对象传输request对象，
//        //5.然后服务端的使用ServerSocket 监控的接口有流传入，创建新线程来接受服务还有socket（里面有request对象）
//        //6.然后使用反射的机制，获得hello方法的返回值，再写入socket
//        //7.最后客户端的socket接受到了返回值，打印出来
//        String res = helloService.hello(object);
//        System.out.println(res);
//    }
public static void main(String[] args) {
    //先创建代理中介对象
    SocketClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER, new RoundRobinLoadBalancer());

    //接口与代理对象之间的中介对象
    RpcClientProxy proxy = new RpcClientProxy(client);
    //创建代理对象
    HelloService helloService = proxy.getProxy(HelloService.class);
    //接口方法的参数对象
    HelloObject object = new HelloObject(12, "This is socket test message");
    //由动态代理可知，代理对象调用hello()实际会执行invoke()
        //由动态代理可知，代理对象调用hello()实际会执行invoke()
    //由动态代理可知，代理对象调用hello()实际会执行invoke()
    String res = helloService.hello(object);
    System.out.println(res);
    ByeService byeService = proxy.getProxy(ByeService.class);
    System.out.println(byeService.bye("Netty"));


}
}
