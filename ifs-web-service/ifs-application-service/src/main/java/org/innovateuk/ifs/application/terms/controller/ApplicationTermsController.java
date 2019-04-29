package org.innovateuk.ifs.application.terms.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/application/{applicationId}/terms-and-conditions")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value="Controller",
        description = "Only applicants are allowed to view the application terms",
        securedType = ApplicationTermsController.class)
public class ApplicationTermsController {

    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;

    public ApplicationTermsController(ApplicationRestService applicationRestService, CompetitionRestService competitionRestService) {
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
    }

    @GetMapping
    public String getTerms(@PathVariable long applicationId, Model model) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        model.addAttribute("template", competition.getTermsAndConditions().getTemplate());
        model.addAttribute("collaborative", application.isCollaborativeProject());
        return "application/terms-and-conditions";
    }
}