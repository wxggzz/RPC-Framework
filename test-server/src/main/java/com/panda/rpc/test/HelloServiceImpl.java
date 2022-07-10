package com.panda.rpc.test;

import com.panda.rpc.annotation.Service;
import com.panda.rpc.api.HelloObject;
import com.panda.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 魏谢哥
 * @description 服务端api接口实现
 */
@Service
public class HelloServiceImpl implements HelloService {
    //使用impl初始化log对象
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到消息：{}", object.getMessage());
        return "成功调用hello()方法";
    }
}
