package org.innovateuk.ifs.application.forms.sections.yourprojectfinances.controller;


import org.innovateuk.ifs.application.forms.sections.yourprojectfinances.populator.YourProjectFinancesModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectfinances.viewmodel.YourProjectFinancesViewModel;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-finances/organisation/{organisationId}/section/{sectionId}")
@Controller
@SecuredBySpring(value = "YOUR_FINANCES", description = "Applicants or internal users can view finances.")
@PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder',  'external_finance')")
public class YourProjectFinancesController {
    private static final String VIEW = "application/sections/your-project-finances/your-project-finances";

    @Autowired
    private YourProjectFinancesModelPopulator yourFinancesModelPopulator;

    @GetMapping
    public String viewFinancesOverview(@PathVariable long applicationId,
                                       @PathVariable long sectionId,
                                       @PathVariable long organisationId,
                                       UserResource user,
                                       Model model) {
        YourProjectFinancesViewModel viewModel = yourFinancesModelPopulator.populate(applicationId, sectionId, organisationId);
        model.addAttribute("model", viewModel);
        return VIEW;
    }
}
