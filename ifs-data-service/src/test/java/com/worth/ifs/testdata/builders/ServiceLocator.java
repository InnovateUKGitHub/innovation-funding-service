package com.worth.ifs.testdata.builders;

import org.springframework.context.support.GenericApplicationContext;

/**
 * A helper class to provide a one-stop lookup of services for components not built by Spring
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
