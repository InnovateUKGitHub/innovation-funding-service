package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.assessment.populator.ManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.navigation.NavigationOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * Controller for the Manage Assessments dashboard.
 */
@Controller
@RequestMapping("/assessment/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class AssessmentController {

    @Autowired
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulator;

    @GetMapping
    public String manageAssessments(@PathVariable("competitionId") long competitionId, Model model,
                                    @RequestParam MultiValueMap<String, String> queryParams) {
        String originQuery = buildOriginQueryString(NavigationOrigin.MANAGE_ASSESSMENTS, queryParams);
        model.addAttribute("model", manageAssessmentsModelPopulator.populateModel(competitionId));
        model.addAttribute("originQuery", originQuery);

        return "competition/manage-assessments";
    }
}