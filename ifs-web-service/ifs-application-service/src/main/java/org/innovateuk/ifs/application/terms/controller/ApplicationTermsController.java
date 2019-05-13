package org.innovateuk.ifs.application.terms.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.terms.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;

@Controller
@RequestMapping("/application/{applicationId}/terms-and-conditions")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "Controller",
        description = "Only applicants are allowed to view the application terms",
        securedType = ApplicationTermsController.class)
public class ApplicationTermsController {

    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;
    private SectionService sectionService;
    private UserRestService userRestService;
    private ApplicationTermsModelPopulator applicationTermsModelPopulator;

    public ApplicationTermsController(ApplicationRestService applicationRestService,
                                      CompetitionRestService competitionRestService,
                                      SectionService sectionService,
                                      UserRestService userRestService,
                                      ApplicationTermsModelPopulator applicationTermsModelPopulator) {
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.sectionService = sectionService;
        this.userRestService = userRestService;
        this.applicationTermsModelPopulator = applicationTermsModelPopulator;
    }

    @GetMapping
    public String getTerms(@PathVariable long applicationId, UserResource user, Model model) {
        model.addAttribute("model", applicationTermsModelPopulator.populate(user, applicationId));
        return "application/terms-and-conditions";
    }

    @PostMapping
    public String acceptTerms(@PathVariable long applicationId, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        SectionResource termsAndConditionsSection = sectionService.getTermsAndConditionsSection(competition.getId());
        ProcessRoleResource processRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        sectionService.markAsComplete(termsAndConditionsSection.getId(), applicationId, processRole.getId());

        return format("redirect:/application/%d/terms-and-conditions", applicationId);
    }
}