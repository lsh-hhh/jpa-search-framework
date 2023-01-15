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

    enum QueryType {
        /** 等于*/
        EQ,
        /** 不等于*/
        NOT_EQ,
        /** 大于*/
        GT,
        /** 大于等于*/
        GE,
        /** 小于等于*/
        LT,
        /** 小于等于*/
        LE,
        /** LIKE*/
        LIKE,
        /** LIKE 左匹配*/
        LEFT_LIKE,
        /** LIKE 右匹配*/
        RIGHT_LIKE,
        /** in*/
        IN,
        /** not in*/
        NOT_IN,
        /** 自定义sql*/
        CUSTOM,
        ;
    }

}

