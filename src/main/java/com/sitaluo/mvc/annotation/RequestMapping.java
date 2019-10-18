package com.sitaluo.mvc.annotation;

import java.lang.annotation.*;

/**
 * 作用于controller类和方法上
 * @author sitaluo
 * date 2019-10-17
 */
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    public String value();
}
