package org.innovateuk.ifs.application.summary.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.summary.populator.ApplicationSummaryViewModelPopulator;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * This controller will handle all requests that are related to the application summary.
 */
@Controller
@RequestMapping("/application")
public class ApplicationSummaryController {

    private ApplicationService applicationService;
    private CompetitionRestService competitionRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator;
    private EuGrantTransferRestService euGrantTransferRestService;

    public ApplicationSummaryController() {
    }

    @Autowired
    public ApplicationSummaryController(ApplicationService applicationService,
                                        CompetitionRestService competitionRestService,
                                        InterviewAssignmentRestService interviewAssignmentRestService,
                                        ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator,
                                        EuGrantTransferRestService euGrantTransferRestService) {
        this.applicationService = applicationService;
        this.competitionRestService = competitionRestService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.applicationSummaryViewModelPopulator = applicationSummaryViewModelPopulator;
        this.euGrantTransferRestService = euGrantTransferRestService;
    }

    @SecuredBySpring(value = "READ", description = "Applicants have permission to view the application summary page")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/summary")
    @AsyncMethod
    public String applicationSummary(Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        if (shouldDisplayFeedback(competition, application)) {
            return redirectToFeedback(applicationId);
        }

        model.addAttribute("model", applicationSummaryViewModelPopulator.populate(application, competition, user));
        return "application-summary";
    }

    private boolean shouldDisplayFeedback(CompetitionResource competition, ApplicationResource application) {
        boolean isApplicationAssignedToInterview = interviewAssignmentRestService.isAssignedToInterview(application.getId()).getSuccess();
        boolean feedbackAvailable = competition.getCompetitionStatus().isFeedbackReleased() || isApplicationAssignedToInterview;
        return application.isSubmitted()
                && feedbackAvailable;
    }

    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads and stakeholders have permission to view the horizon 2020 grant agreement")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'stakeholder', 'comp_admin', 'project_finance')")
    @GetMapping("/{applicationId}/grant-agreement")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadGrantAgreement(@PathVariable long applicationId) {
        return getFileResponseEntity(euGrantTransferRestService.downloadGrantAgreement(applicationId).getSuccess(),
                euGrantTransferRestService.findGrantAgreement(applicationId).getSuccess());
    }

    private String redirectToFeedback(long applicationId) {
        return UriComponentsBuilder.fromPath(String.format("redirect:/application/%s/feedback", applicationId))
                .build()
                .encode()
                .toUriString();
    }
}
