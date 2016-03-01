package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.service.ProcessRoleService;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class JESFinanceModelManager implements FinanceModelManager {

    @Autowired
    ProcessRoleService processRoleService;

    @Override
    public void addOrganisationFinanceDetails(Model model, Long applicationId, Long userId, Form form) {
        ProcessRole processRole = processRoleService.findProcessRole(userId, applicationId);
        String organisationName = processRole.getOrganisation().getName();
        model.addAttribute("title", organisationName + " finances");
        model.addAttribute("financeView", "academic-finance");
    }

    @Override
    public void addCost(Model model, CostItem costItem, long applicationId, long userId, Long questionId, String costType) {

    }
}
