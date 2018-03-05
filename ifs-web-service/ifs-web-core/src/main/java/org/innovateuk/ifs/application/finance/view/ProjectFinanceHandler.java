package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.view.DefaultProjectFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.view.FinanceModelManager;
import org.innovateuk.ifs.application.finance.view.jes.JESFinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.jes.JESProjectFinanceModelManager;
import org.innovateuk.ifs.project.finance.view.ProjectFinanceFormHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.user.resource.OrganisationTypeEnum.RESEARCH;

@Component
@Configuration
public class ProjectFinanceHandler implements FinanceHandler {

    public FinanceFormHandler getFinanceFormHandler(Long organisationType) {
        return null;
    }

    public FinanceFormHandler getProjectFinanceFormHandler(Long organisationType) {
        if(RESEARCH.getId().equals(organisationType)) {
            return getJESFinanceFormHandler();
        } else {
            return getProjectFinanceFormHandler();
        }
    }

    public FinanceModelManager getFinanceModelManager(Long organisationType) {
        return null;
    }


    public FinanceModelManager getProjectFinanceModelManager(Long organisationType) {
        if(RESEARCH.getId().equals(organisationType)) {
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
    protected FinanceModelManager getJESProjectFinanceModelManager() {
        return new JESProjectFinanceModelManager();
    }

    @Bean
    protected FinanceModelManager getDefaultProjectFinanceModelManager() {
        return new DefaultProjectFinanceModelManager();
    }

    @Bean
    protected FinanceFormHandler getProjectFinanceFormHandler() {
        return new ProjectFinanceFormHandler();
    }
}

