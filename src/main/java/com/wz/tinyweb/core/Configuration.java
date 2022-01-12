package com.wz.tinyweb.core;

import java.lang.annotation.*;

@Documented
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Configuration {
    String value() default "";
}
