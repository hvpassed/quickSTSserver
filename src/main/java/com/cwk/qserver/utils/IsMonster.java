package com.cwk.qserver.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.utils
 * @Author: chen wenke
 * @CreateTime: 2023-11-29 12:04
 * @Description: TODO
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IsMonster {
}
