package org.innovateuk.ifs.application.finance.view;

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
    private DefaultFinanceFormHandler defaultFinanceFormHandler;


    public FinanceFormHandler getFinanceFormHandler(CompetitionResource competition, long organisationType) {
        return defaultFinanceFormHandler;
    }

    public FinanceModelManager getFinanceModelManager(CompetitionResource competition, long organisationType) {
        return getDefaultFinanceModelManager();
    }

    @Bean
    protected FinanceModelManager getDefaultFinanceModelManager() {
        return new DefaultFinanceModelManager();
    }

}