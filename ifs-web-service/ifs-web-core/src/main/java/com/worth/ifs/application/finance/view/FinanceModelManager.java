package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.util.List;

import org.springframework.ui.Model;

public interface FinanceModelManager {
    void addOrganisationFinanceDetails(Model model, Long applicationId, List<QuestionResource> costsQuestions, Long userId, Form form);
    void addCost(Model model, CostItem costItem, long applicationId, long userId, Long questionId, String costType);
}
