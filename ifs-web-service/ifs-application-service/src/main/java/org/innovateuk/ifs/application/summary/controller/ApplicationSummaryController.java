package org.innovateuk.ifs.application.summary.controller;

import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.summary.populator.ApplicationSummaryViewModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import static org.innovateuk.ifs.user.resource.Role.SUPPORT;

/**
 * This controller will handle all requests that are related to the application summary.
 */
@Controller
@RequestMapping("/application")
public class ApplicationSummaryController {

    private ApplicationService applicationService;
    private UserService userService;
    private CompetitionRestService competitionRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator;

    public ApplicationSummaryController() {
    }

    @Autowired
    public ApplicationSummaryController(ApplicationService applicationService,
                                        UserService userService,
                                        CompetitionRestService competitionRestService,
                                        InterviewAssignmentRestService interviewAssignmentRestService,
                                        ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.competitionRestService = competitionRestService;
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
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        boolean isApplicationAssignedToInterview = interviewAssignmentRestService.isAssignedToInterview(applicationId).getSuccess();

        boolean isSupport = isSupport(user);
        if (competition.getCompetitionStatus().isFeedbackReleased() || isApplicationAssignedToInterview) {
            return redirectToFeedback(applicationId, queryParams);
        }

        UserResource userForModel;
        if (isSupport) {
            ProcessRoleResource leadProcessRoleResource = userService.getLeadApplicantProcessRoleOrNull(applicationId);
            userForModel = userService.findById(leadProcessRoleResource.getUser());
        } else {
            userForModel = user;
        }

        model.addAttribute("model", applicationSummaryViewModelPopulator.populate(applicationId, userForModel, form, isSupport));
        return "application-summary";
    }

    private boolean isSupport(UserResource user) {
        return user.hasRole(SUPPORT);
    }

    private String redirectToFeedback(long applicationId, MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder.fromPath(String.format("redirect:/application/%s/feedback", applicationId))
                .queryParams(queryParams)
                .build()
                .encode()
                .toUriString();
    }
}
