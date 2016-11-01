package com.worth.ifs.testdata;

import org.springframework.context.support.GenericApplicationContext;

/**
 * TODO DW - document this class
 */
public class ServiceLocator {

    private GenericApplicationContext applicationContext;

    public ServiceLocator(GenericApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <T>T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
