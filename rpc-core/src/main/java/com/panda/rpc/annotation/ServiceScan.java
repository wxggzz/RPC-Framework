package com.panda.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wxg
 * @version 1.0
 * @date 2022/6/13 23:35
 * @description 标识服务的扫描的包的范围,即扫描范围的根包
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceScan {
    //默认值为空,
    public String value() default "";

}
