package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.viewmodel.BaseFinanceViewModel;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.ui.Model;

import java.util.List;

public interface FinanceModelManager {
    //TODO: INFUND-7849 - make sure this function is not going to be used anymore
    void addOrganisationFinanceDetails(Model model, Long applicationId, List<QuestionResource> costsQuestions, Long userId, Form form, Long organisationId);

    BaseFinanceViewModel getFinanceViewModel(Long targetId, List<QuestionResource> costsQuestions, Long userId, Form form, Long organisationId);

    void addCost(Model model, FinanceRowItem costItem, long applicationId, long organisationId, long userId, Long questionId, FinanceRowType costType);
}
