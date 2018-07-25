package org.innovateuk.ifs.application.finance;

public interface FinanceHandlerProvider {

    FinanceFormHandler getFinanceFormHandler(long organisationType);
    FinanceFormHandler getProjectFinanceFormHandler(long organisationType);
    FinanceModelManager getFinanceModelManager(long organisationType);
    FinanceModelManager getProjectFinanceModelManager(long organisationType);

}
