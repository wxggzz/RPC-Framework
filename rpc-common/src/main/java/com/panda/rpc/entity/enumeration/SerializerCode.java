package com.panda.rpc.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: wxg
 * @Date: 2022/06/02/17:52
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {
    //序列化器的编号，KRYO为1
    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);


    private final int code;
}
