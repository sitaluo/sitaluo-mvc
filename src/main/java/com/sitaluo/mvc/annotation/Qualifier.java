package com.sitaluo.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author sitaluo
 * date 2019-10-17
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    public String value();
}
