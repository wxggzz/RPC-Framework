package com.panda.rpc.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/05/27/17:43
 * @Description: rpc调用过程中出现的错误提示
 */
@AllArgsConstructor
@Getter
public enum RpcError {

    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),
    UNKNOWN_PROTOCOL("不识别的协议包"),
    UNKNOWN_SERIALIZER("不识别的(反)序列化器"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型"),
    SERIALIZER_NOT_FOUND("找不到序列化器"),
    RESPONSE_NOT_MATCH("响应与请求号不匹配"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接注册中心失败"),
    REGISTER_SERVICE_FAILED("注册服务失败"),
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败"),
    UNKNOWN_ERROR("出现未知错误"),
    SERVICE_SCAN_PACKAGE_NOT_FOUND("启动类ServiceScan注解缺失");

    private final String message;
}
