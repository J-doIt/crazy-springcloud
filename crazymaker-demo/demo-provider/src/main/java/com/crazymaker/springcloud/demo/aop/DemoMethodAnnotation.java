package com.crazymaker.springcloud.demo.aop;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DemoMethodAnnotation {
    String name() default "";
}
