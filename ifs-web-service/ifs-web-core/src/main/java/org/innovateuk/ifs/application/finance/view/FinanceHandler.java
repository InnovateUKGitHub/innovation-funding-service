package org.innovateuk.ifs.application.finance.view;

public interface FinanceHandler {

    FinanceFormHandler getFinanceFormHandler(Long organisationType);
    FinanceFormHandler getProjectFinanceFormHandler(Long organisationType);
    FinanceModelManager getFinanceModelManager(Long organisationType);
    FinanceModelManager getProjectFinanceModelManager(Long organisationType);

}
