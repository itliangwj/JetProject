package com.jet.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @author Jet
 * @version 1.0
 * @description: TODO
 * @date 2023/6/4 8:48
 */
//@Target 决定注解的适用范围
@Target({ElementType.FIELD})
//@Retention 决定注解的生命周期，Runtime表示整个程序运行期间皆有效
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JAutowired {
    String value() default "";
}
