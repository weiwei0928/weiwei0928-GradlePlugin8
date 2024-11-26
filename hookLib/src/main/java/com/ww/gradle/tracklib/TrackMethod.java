package com.ww.gradle.tracklib;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要打桩的注解
 * @Author weiwei
 * @Date 2024/11/23 18:28
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface TrackMethod {
}

