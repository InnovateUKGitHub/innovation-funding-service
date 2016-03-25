package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.view.jes.JESFinanceFormHandler;
import com.worth.ifs.application.finance.view.jes.JESFinanceModelManager;
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

    public FinanceModelManager getFinanceModelManager(String organisationType) {
        switch(organisationType) {
            case "University (HEI)":
                return getJESFinanceModelManager();
            default:
                return getDefaultFinanceModelManager();
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
    protected FinanceModelManager getJESFinanceModelManager() {
        return new JESFinanceModelManager();
    }

    @Bean
    protected FinanceModelManager getDefaultFinanceModelManager() {
        return new DefaultFinanceModelManager();
    }
}


