/**
 * @author wxg
 * @version 1.0
 * @date 2022/6/18 19:47
 */
public class DemoImpl implements DemoInterface {
    @Override
    public String demoHello(String msg) {
        System.out.println("调用方法了,传入的参数为："+msg);
        return "hello";
    }
}
