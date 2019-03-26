package org.innovateuk.ifs.application.summary.controller;

import org.innovateuk.ifs.application.forms.form.ApplicationSubmitForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.summary.populator.ApplicationSummaryViewModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.origin.ApplicationSummaryOrigin;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.innovateuk.ifs.application.forms.controller.ApplicationSubmitController.APPLICATION_SUBMIT_FROM_ATTR_NAME;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;

/**
 * This controller will handle all requests that are related to the application summary.
 */
@Controller
@RequestMapping("/application")
public class ApplicationSummaryController {

    private ApplicationService applicationService;
    private UserService userService;
    private UserRestService userRestService;
    private CompetitionRestService competitionRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator;
    private EuGrantTransferRestService euGrantTransferRestService;

    public ApplicationSummaryController() {
    }

    @Autowired
    public ApplicationSummaryController(ApplicationService applicationService, UserService userService,
                                        UserRestService userRestService, CompetitionRestService competitionRestService,
                                        InterviewAssignmentRestService interviewAssignmentRestService,
                                        ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator,
                                        EuGrantTransferRestService euGrantTransferRestService) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.userRestService = userRestService;
        this.competitionRestService = competitionRestService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.applicationSummaryViewModelPopulator = applicationSummaryViewModelPopulator;
        this.euGrantTransferRestService = euGrantTransferRestService;
    }

    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads and stakeholders have permission to view the application summary page")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'stakeholder', 'monitoring_officer')")
    @GetMapping("/{applicationId}/summary")
    public String applicationSummary(@ModelAttribute("form") ApplicationForm form,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user,
                                     @RequestParam(value = "origin", defaultValue = "APPLICATION") String origin,
                                     @RequestParam MultiValueMap<String, String> queryParams) {

        if (!model.containsAttribute(APPLICATION_SUBMIT_FROM_ATTR_NAME)) {
            model.addAttribute(APPLICATION_SUBMIT_FROM_ATTR_NAME, new ApplicationSubmitForm());
        }

        String originQuery = buildOriginQueryString(ApplicationSummaryOrigin.valueOf(origin), queryParams);

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        boolean isApplicationAssignedToInterview = interviewAssignmentRestService.isAssignedToInterview(applicationId).getSuccess();

        boolean isSupport = isSupport(user);
        if ((competition.getCompetitionStatus().isFeedbackReleased() || isApplicationAssignedToInterview) && !isSupport) {
            return redirectToFeedback(applicationId, queryParams);
        }

        UserResource userForModel;
        if (isSupport) {
            ProcessRoleResource leadProcessRoleResource = userService.getLeadApplicantProcessRoleOrNull(applicationId);
            userForModel = userRestService.retrieveUserById(leadProcessRoleResource.getUser()).getSuccess();
        } else {
            userForModel = user;
        }

        model.addAttribute("originQuery", originQuery);
        model.addAttribute("model", applicationSummaryViewModelPopulator.populate(application, competition, userForModel, form, isSupport));
        return "application-summary";
    }

    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads and stakeholders have permission to view the horizon 2020 grant agreement")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'stakeholder', 'comp_admin', 'project_finance')")
    @GetMapping("/{applicationId}/grant-agreement")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadGrantAgreement(@PathVariable long applicationId) {
        return getFileResponseEntity(euGrantTransferRestService.downloadGrantAgreement(applicationId).getSuccess(),
                euGrantTransferRestService.findGrantAgreement(applicationId).getSuccess());
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
