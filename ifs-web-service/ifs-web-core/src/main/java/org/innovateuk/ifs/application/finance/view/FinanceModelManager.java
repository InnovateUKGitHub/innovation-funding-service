package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import java.util.List;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.ui.Model;

public interface FinanceModelManager {
    void addOrganisationFinanceDetails(Model model, Long applicationId, List<QuestionResource> costsQuestions, Long userId, Form form);
    void addCost(Model model, FinanceRowItem costItem, long applicationId, long userId, Long questionId, FinanceRowType costType);
}
