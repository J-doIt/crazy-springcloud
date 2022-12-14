package com.crazymaker.springcloud.demo.aop;



import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DemoTypeAnnotation {
    String name() default "";
}
