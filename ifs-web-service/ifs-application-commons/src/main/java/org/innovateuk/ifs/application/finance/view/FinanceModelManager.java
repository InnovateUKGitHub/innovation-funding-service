package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.viewmodel.BaseFinanceViewModel;
import org.innovateuk.ifs.form.Form;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.util.List;

public interface FinanceModelManager {

    BaseFinanceViewModel getFinanceViewModel(Long targetId, List<QuestionResource> costsQuestions, Long userId, Form form, Long organisationId);

}
