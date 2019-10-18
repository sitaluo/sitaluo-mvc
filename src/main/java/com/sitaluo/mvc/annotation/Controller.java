package com.sitaluo.mvc.annotation;

import java.lang.annotation.*;

/**
 * 作用在Controller类上的注解
 * @author sitaluo
 * date 2019-10-17
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    /**
     * @return 在ioc容器中的名称
     */
    public String value();
}
