package com.cwk.qserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.utils
 * @Author: chen wenke
 * @CreateTime: 2023-11-29 00:06
 * @Description: TODO
 * @Version: 1.0
 */

@Component
@Slf4j
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
    public static ApplicationContext getApplicationContext(){
        return context;
    }
    /**
     * 通过name获取 Bean
     * @param name beanName
     * @return Object
     */
    public static Object getBean(String name){
        log.debug("Bean "+name+" getting");
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException{
        if (requiredType!=null) {
            log.debug("Bean " +requiredType.toString()+ " getting");
        }
        if (requiredType != null) {
            return getApplicationContext().getBean(requiredType);
        }else {
            return null;
        }
    }
}