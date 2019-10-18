package com.sitaluo.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author sitaluo
 * date 2019-10-17
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    public String value();
}
