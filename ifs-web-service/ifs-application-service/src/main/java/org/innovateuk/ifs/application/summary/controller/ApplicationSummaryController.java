package org.innovateuk.ifs.application.summary.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.summary.populator.ApplicationSummaryViewModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This controller will handle all requests that are related to the application summary.
 */
@Controller
@RequestMapping("/application")
public class ApplicationSummaryController {


    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator;

    public ApplicationSummaryController() {
    }

    @Autowired
    public ApplicationSummaryController(ApplicationService applicationService,
                                        CompetitionService competitionService,
                                        InterviewAssignmentRestService interviewAssignmentRestService,
                                        ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator) {
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.applicationSummaryViewModelPopulator = applicationSummaryViewModelPopulator;
    }

    @SecuredBySpring(value = "READ", description = "Applicants, support staff, and innovation leads have permission to view the application summary page")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead')")
    @GetMapping("/{applicationId}/summary")
    public String applicationSummary(@ModelAttribute("form") ApplicationForm form,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user,
                                     @RequestParam MultiValueMap<String, String> queryParams) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        boolean isApplicationAssignedToInterview = interviewAssignmentRestService.isAssignedToInterview(applicationId).getSuccess();
        if (competition.getCompetitionStatus().isFeedbackReleased() || isApplicationAssignedToInterview) {
            return redirectToFeedback(applicationId, queryParams);
        }
        model.addAttribute("model", applicationSummaryViewModelPopulator.populate(applicationId, user, form));
        return "application-summary";
    }

    private String redirectToFeedback(long applicationId, MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder.fromPath(String.format("redirect:/application/%s/feedback", applicationId))
                .queryParams(queryParams)
                .build()
                .encode()
                .toUriString();
    }
}
