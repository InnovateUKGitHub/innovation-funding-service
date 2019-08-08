package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.viewmodel.BaseFinanceViewModel;
import org.innovateuk.ifs.form.Form;

public interface FinanceModelManager {

    BaseFinanceViewModel getFinanceViewModel(Long targetId, Long userId, Form form, Long organisationId);

}
