package org.innovateuk.ifs.application.forms.sections.yourfinances.controller;


import org.innovateuk.ifs.application.forms.sections.financesoverview.viewmodel.FinancesOverviewViewModel;
import org.innovateuk.ifs.application.forms.sections.yourfinances.populator.YourFinancesModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel.YourFinancesViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-finances/section/{sectionId}")
@Controller
public class YourFinancesController {
    private static final String VIEW = "application/sections/your-finances/your-finances";

    @Autowired
    private YourFinancesModelPopulator yourFinancesModelPopulator;

    @GetMapping
    public String viewFinancesOverview(@PathVariable long applicationId,
                                       @PathVariable long sectionId,
                                       UserResource user,
                                       Model model) {
        YourFinancesViewModel viewModel = yourFinancesModelPopulator.populate(applicationId, sectionId, user);
        model.addAttribute("model", viewModel);
        return VIEW;
    }
}
