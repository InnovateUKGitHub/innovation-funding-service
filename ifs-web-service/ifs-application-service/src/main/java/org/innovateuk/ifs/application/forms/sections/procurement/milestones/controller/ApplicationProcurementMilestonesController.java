package org.innovateuk.ifs.application.forms.sections.procurement.milestones.controller;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel.ApplicationProcurementMilestonesViewModel;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationProcurementMilestonesController {
    private static final String VIEW = "application/sections/procurement-milestones/procurement-milestones";


    @Autowired
    private ProcurementMilestoneFormPopulator formPopulator;

    @Autowired
    private ApplicationProcurementMilestoneRestService restService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @GetMapping
    public String viewMilestones(@PathVariable long applicationId,
                                 @PathVariable long organisationId,
                                 Model model) {
        model.addAttribute("form", formPopulator.populate(restService.getByApplicationIdAndOrganisationId(applicationId, organisationId).getSuccess()));
        model.addAttribute("model", new ApplicationProcurementMilestonesViewModel(applicationRestService.getApplicationById(applicationId).getSuccess(),
                String.format("/application/%d/form/FINANCE/%d", applicationId, organisationId)));
        return VIEW;
    }

}
