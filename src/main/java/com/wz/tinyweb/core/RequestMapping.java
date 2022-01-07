package com.wz.tinyweb.core;

import java.lang.annotation.*;

@Documented
@Target(value = {ElementType.TYPE,ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();
}
