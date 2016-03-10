package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.form.Form;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.springframework.ui.Model;

public interface FinanceModelManager {
    void addOrganisationFinanceDetails(Model model, Long applicationId, Long userId, Form form);
    void addCost(Model model, CostItem costItem, long applicationId, long userId, Long questionId, String costType);
}
