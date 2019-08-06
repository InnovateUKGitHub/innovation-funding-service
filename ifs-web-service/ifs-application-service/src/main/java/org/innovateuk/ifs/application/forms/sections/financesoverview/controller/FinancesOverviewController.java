package org.innovateuk.ifs.application.forms.sections.financesoverview.controller;

import org.innovateuk.ifs.application.forms.sections.financesoverview.populator.FinancesOverviewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.financesoverview.viewmodel.FinancesOverviewViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/finances-overview/section/{sectionId}")
@Controller
public class FinancesOverviewController {
    private static final String VIEW = "application/sections/finances-overview/finances-overview";

    @Autowired
    private FinancesOverviewModelPopulator financesOverviewModelPopulator;

    @GetMapping
    public String viewFinancesOverview(@PathVariable long applicationId,
                                       @PathVariable long sectionId,
                                       UserResource user,
                                       Model model) {
        FinancesOverviewViewModel viewModel = financesOverviewModelPopulator.populate(applicationId, sectionId, user);
        model.addAttribute("model", viewModel);
        return VIEW;
    }
}
