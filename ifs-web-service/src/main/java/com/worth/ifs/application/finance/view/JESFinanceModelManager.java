package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.form.Form;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class JESFinanceModelManager implements FinanceModelManager {

    @Override
    public void addOrganisationFinanceDetails(Model model, Long applicationId, Long userId, Form form) {
        model.addAttribute("financeView", "academic-finance");
    }

    @Override
    public void addCost(Model model, CostItem costItem, long applicationId, long userId, Long questionId, String costType) {

    }
}
