package com.panvan.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * describe:
 * author:jpw
 * Date:2020/4/14
 * Time:11:45
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AppRequestMapper {
	String path() default "";

	AppRequestMethod method() default AppRequestMethod.GET;
}

