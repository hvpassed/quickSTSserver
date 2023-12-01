package com.cwk.qserver.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.utils
 * @Author: chen wenke
 * @CreateTime: 2023-11-30 19:05
 * @Description: TODO
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IsCard {
    int cardId() default 0;
}
