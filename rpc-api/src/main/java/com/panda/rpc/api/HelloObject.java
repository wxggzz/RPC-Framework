package com.panda.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
//加上get set hashcode等方法
@Data
@AllArgsConstructor
@NoArgsConstructor
//定义发送目标的信息，得实现序列化，即implements Serializable
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
