package org.innovateuk.ifs.testdata.builders;

import org.springframework.context.support.GenericApplicationContext;

/**
 * A helper class to provide a one-stop lookup of services for components not built by Spring
 */
public class ServiceLocator {

    private GenericApplicationContext applicationContext;

    private String compAdminEmail;
    private String projectFinanceEmail;

    public ServiceLocator(GenericApplicationContext applicationContext, String compAdminEmail, String projectFinanceEmail) {
        this.applicationContext = applicationContext;
        this.compAdminEmail = compAdminEmail;
        this.projectFinanceEmail = projectFinanceEmail;
    }

    public <T>T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public String getCompAdminEmail() {
        return compAdminEmail;
    }

    public String getProjectFinanceEmail() {
        return projectFinanceEmail;
    }
}
