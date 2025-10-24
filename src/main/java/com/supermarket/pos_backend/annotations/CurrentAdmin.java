package com.supermarket.pos_backend.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)       // usable on controller parameters
@Retention(RetentionPolicy.RUNTIME)  // available at runtime
public @interface CurrentAdmin {boolean required() default true;}
