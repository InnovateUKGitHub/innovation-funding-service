package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.assessment.dashboard.form.AssessorCompetitionDashboardAssessmentForm;
import org.innovateuk.ifs.assessment.dashboard.populator.AssessorCompetitionForPanelDashboardModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to the assessor panel dashboard.
 */
@Controller
@RequestMapping(value = "/assessor")
@SecuredBySpring(value = "Controller", description = "Assessors can access the assessment panel dashboard", securedType = AssessorCompetitionForPanelDashboardController.class)
@PreAuthorize("hasAuthority('assessor')")
public class AssessorCompetitionForPanelDashboardController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private AssessorCompetitionForPanelDashboardModelPopulator assessorCompetitionForPanelDashboardModelPopulator;

    @GetMapping("/dashboard/competition/{competitionId}/panel")
    public String competitionDashboard(final Model model,
                                       UserResource loggedInUser,
                                       @PathVariable("competitionId") final long competitionId,
                                       @ModelAttribute(name = FORM_ATTR_NAME, binding = false) AssessorCompetitionDashboardAssessmentForm form) {

        model.addAttribute("model", assessorCompetitionForPanelDashboardModelPopulator.populateModel(competitionId, loggedInUser.getId()));
        return "assessor-competition-for-panel-dashboard";
    }
}
