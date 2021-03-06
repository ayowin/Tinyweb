package com.wz.tinyweb.core;

import java.lang.annotation.*;

@Documented
@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Inject {
    String value() default "";
}
