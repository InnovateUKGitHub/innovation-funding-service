package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.view.jes.JESFinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.jes.JESFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.jes.JESProjectFinanceModelManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;

/**
 * Finance handler for application and project
 */
@Component
@Configuration
public class FinanceViewHandlerProvider implements FinanceHandlerProvider {

    public FinanceFormHandler getFinanceFormHandler(long organisationType) {
        if(RESEARCH.getId().longValue() == organisationType) {
            return getJESFinanceFormHandler();
        } else {
            return getDefaultFinanceFormHandler();
        }
    }

    public FinanceFormHandler getProjectFinanceFormHandler(long organisationType) {
        if(RESEARCH.getId().longValue() == organisationType) {
            return getJESFinanceFormHandler();
        } else {
            return getProjectFinanceFormHandler();
        }
    }

    public FinanceModelManager getFinanceModelManager(long organisationType) {
        if(RESEARCH.getId().longValue() == organisationType) {
            return getJESFinanceModelManager();
        } else {
            return getDefaultFinanceModelManager();
        }
    }

    public FinanceModelManager getProjectFinanceModelManager(long organisationType) {
        if(RESEARCH.getId().longValue() == organisationType) {
            return getJESProjectFinanceModelManager();
        } else {
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