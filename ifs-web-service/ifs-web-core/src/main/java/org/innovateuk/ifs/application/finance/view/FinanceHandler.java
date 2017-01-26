package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.view.jes.JESFinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.jes.JESFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.jes.JESProjectFinanceModelManager;
import org.innovateuk.ifs.project.finance.view.ProjectFinanceFormHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class FinanceHandler {

    public FinanceFormHandler getFinanceFormHandler(String organisationType) {
        switch(organisationType) {
            case "University (HEI)":
                return getJESFinanceFormHandler();
            default:
                return getDefaultFinanceFormHandler();
        }
    }

    public FinanceFormHandler getProjectFinanceFormHandler(String organisationType) {
        switch(organisationType) {
            case "University (HEI)":
                return getJESFinanceFormHandler();
            default:
                return getProjectFinanceFormHandler();
        }
    }

    public FinanceModelManager getFinanceModelManager(String organisationType) {
        switch(organisationType) {
            case "University (HEI)":
                return getJESFinanceModelManager();
            default:
                return getDefaultFinanceModelManager();
        }
    }

    public FinanceModelManager getProjectFinanceModelManager(String organisationType) {
        switch(organisationType) {
            case "University (HEI)":
                return getJESProjectFinanceModelManager();
            default:
                return getDefaultProjectFinanceModelManager();
        }
    }

    @Bean
    protected FinanceFormHandler getJESFinanceFormHandler() {
        return new JESFinanceFormHandler();
    }

    @Bean
    protected FinanceFormHandler getDefaultFinanceFormHandler() {
        return new DefaultFinanceFormHandler();
    }

    @Bean
    protected FinanceFormHandler getProjectFinanceFormHandler() {
        return new ProjectFinanceFormHandler();
    }

    @Bean
    protected FinanceModelManager getJESFinanceModelManager() {
        return new JESFinanceModelManager();
    }

    @Bean
    protected FinanceModelManager getDefaultFinanceModelManager() {
        return new DefaultFinanceModelManager();
    }

    @Bean
    protected FinanceModelManager getJESProjectFinanceModelManager() {
        return new JESProjectFinanceModelManager();
    }

    @Bean
    protected FinanceModelManager getDefaultProjectFinanceModelManager() {
        return new DefaultProjectFinanceModelManager();
    }
}


