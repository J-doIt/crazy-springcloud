package com.crazymaker.springcloud.demo.aop;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class DemoPointCut implements Pointcut {

    //类级别的目标过滤
    @Override
    public ClassFilter getClassFilter() {

        return new MyClassFilter();
    }
    //方法级别的目标过滤
    @Override
    public MethodMatcher getMethodMatcher() {

        return new MyMethodMatcher();
    }

    private class MyMethodMatcher implements MethodMatcher {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            Annotation[] annoArray = method.getDeclaredAnnotations();
            if (annoArray == null || annoArray.length == 0) {
                return false;
            }

            for (Annotation annotation : annoArray) {
                if (annotation.annotationType() == DemoMethodAnnotation.class) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return false;
        }
    }

    private class MyClassFilter implements ClassFilter {

        @Override
        public boolean matches(Class<?> clazz) {
            boolean isMatch =   clazz.isAnnotationPresent(DemoTypeAnnotation.class);
            if (isMatch) {
                return true;
            }

            return false;
        }
    }
}
