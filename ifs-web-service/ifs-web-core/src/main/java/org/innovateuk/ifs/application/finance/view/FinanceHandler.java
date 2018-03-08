package org.innovateuk.ifs.application.finance.view;

public interface FinanceHandler {

    FinanceFormHandler getFinanceFormHandler(long organisationType);
    FinanceFormHandler getProjectFinanceFormHandler(long organisationType);
    FinanceModelManager getFinanceModelManager(long organisationType);
    FinanceModelManager getProjectFinanceModelManager(long organisationType);

}
