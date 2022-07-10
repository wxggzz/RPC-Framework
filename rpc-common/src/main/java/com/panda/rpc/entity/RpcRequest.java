package com.panda.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
//同时也需要序列化，因此是需要传输的对象
public class RpcRequest implements Serializable {
    /**
     * 请求号
     */
    private String requestId;
    /**
     * 接口名称
     */
    private String interfaceName;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     *待调用方法的参数,放到一个Object数组里面
     */

    private Object[] parameters;
    /**
     * 待调用参数的类型
     */
    //Class<?>它是个通配泛型，? 可以代表任何类型，所以主要用于声明时的限制情况
            //声明什么类型的 Class 的时候可以定义一 个Class
            //,定义一个paramTypes的引用变量数组，就可以存储多个不同类型对象。
            //限制通配符
    /**
     *
     * List<T> getList<T param1,T param2>
     * 这样可以限制返回结果的类型以及两个参数的类型一致。,
     * //Class<?>一般就是在泛型起一个限制作用。
     * ? 表示不确定的类型，一般用在通配
     * T的意义是自定义范型，?表示通配符范型。
     * 此处的？和Number、String、Integer一样都是一种实际的类型，可以把？看成所有类型的父类。是一种真实的类型。通常用于泛型方法的调用代码和形参，不能用于定义类和泛型方法
     */
    private Class<?>[] paramTypes;
    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;
}
