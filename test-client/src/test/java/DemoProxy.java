import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author wxg
 * @version 1.0
 * @date 2022/6/18 19:48
 */
public class DemoProxy implements InvocationHandler {
    //注入接口
    private DemoInterface service;
    public DemoProxy(DemoInterface service){
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("调用方法前");
        Object invoke = method.invoke(service, args);
        System.out.println("调用方法后");
        return invoke;
    }
}
