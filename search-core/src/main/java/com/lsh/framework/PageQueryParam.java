package com.lsh.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PageQueryParam {

    QueryType type();

    String sqlKeyword() default "";

    String customSql() default "";

    public static enum QueryType {
        EQUAL,
        NOT_EQUAL,
        GREAT,
        GREAT_OR_EQUAL,
        LESS,
        LESS_OR_EQUAL,
        LIKE,
        LEFT_LIKE,
        RIGHT_LIKE,
        IN,
        NOT_IN,
        CUSTOM;
    }

}

