package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.view.jes.JESFinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.jes.JESFinanceModelManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.user.resource.OrganisationTypeEnum.RESEARCH;

@Component
@Configuration
public class ApplicationFinanceHandler implements FinanceHandler {

    public FinanceFormHandler getFinanceFormHandler(Long organisationType) {
        if(RESEARCH.getId().equals(organisationType)) {
            return getJESFinanceFormHandler();
        } else {
            return getDefaultFinanceFormHandler();
        }
    }

    public FinanceFormHandler getProjectFinanceFormHandler(Long organisationType) {
        return null;
    }

    public FinanceModelManager getFinanceModelManager(Long organisationType) {
        if(RESEARCH.getId().equals(organisationType)) {
            return getJESFinanceModelManager();
        } else {
            return getDefaultFinanceModelManager();
        }
    }


    public FinanceModelManager getProjectFinanceModelManager(Long organisationType) {
        return null;
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
    protected FinanceModelManager getJESFinanceModelManager() {
        return new JESFinanceModelManager();
    }

    @Bean
    protected FinanceModelManager getDefaultFinanceModelManager() {
        return new DefaultFinanceModelManager();
    }
}

