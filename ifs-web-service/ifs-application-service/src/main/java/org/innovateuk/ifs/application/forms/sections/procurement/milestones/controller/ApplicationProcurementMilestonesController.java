package org.innovateuk.ifs.application.forms.sections.procurement.milestones.controller;

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


    @GetMapping
    public String viewMilestones(@PathVariable long applicationId,
                                 @PathVariable long organisationId,
                                 Model model,
                                 ) {

    }

}
