package com.panda.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/08/22:47
 * @Description:  轮转算法,按顺序来
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    /**
     * index表示当前选到了第几个服务器，并且每次选择后都会自增一
     */
    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if(index >= instances.size()){
            index %= instances.size();
        }
        return instances.get(index++);
    }
}
