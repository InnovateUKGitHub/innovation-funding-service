package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.view.jes.JESFinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.jes.JESFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.jes.JESProjectFinanceModelManager;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Finance handler for application and project
 */
@Component
@Configuration
public class FinanceViewHandlerProvider implements FinanceHandlerProvider {

    @Autowired
    private ProjectFinanceFormHandler projectFinanceFormHandler;

    @Autowired
    private DefaultFinanceFormHandler defaultFinanceFormHandler;

    @Autowired
    private JESFinanceFormHandler jesFinanceFormHandler;

    public FinanceFormHandler getFinanceFormHandler(CompetitionResource competition, long organisationType) {
        if(competition.showJesFinances(organisationType)) {
            return jesFinanceFormHandler;
        } else {
            return defaultFinanceFormHandler;
        }
    }

    public FinanceFormHandler getProjectFinanceFormHandler(CompetitionResource competition, long organisationType) {
        if(competition.showJesFinances(organisationType)) {
            return jesFinanceFormHandler;
        } else {
            return projectFinanceFormHandler;
        }
    }

    public FinanceModelManager getFinanceModelManager(CompetitionResource competition, long organisationType) {
        if(competition.showJesFinances(organisationType)) {
            return getJESFinanceModelManager();
        } else {
            return getDefaultFinanceModelManager();
        }
    }

    public FinanceModelManager getProjectFinanceModelManager(CompetitionResource competition, long organisationType) {
        if(competition.showJesFinances(organisationType)) {
            return getJESProjectFinanceModelManager();
        } else {
            return getDefaultProjectFinanceModelManager();
        }
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