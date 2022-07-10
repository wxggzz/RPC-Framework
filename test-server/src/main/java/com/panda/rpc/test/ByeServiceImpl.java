package com.panda.rpc.test;

import com.panda.rpc.annotation.Service;
import com.panda.rpc.api.ByeService;

/**
 * @author wxg
 * @version 1.0
 * @date 2022/6/14 19:53
 * @description 服务实现类
 */
@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye," + name;
    }
}
