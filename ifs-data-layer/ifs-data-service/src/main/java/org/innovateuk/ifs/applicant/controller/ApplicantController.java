package org.innovateuk.ifs.applicant.controller;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.transactional.ApplicantService;
import org.innovateuk.ifs.application.transactional.ApplicationDashboardService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ApplicantController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/applicant")
public class ApplicantController {

    private ApplicantService applicantService;
    private ApplicationDashboardService applicationDashboardService;

    public ApplicantController() {
    }

    @Autowired
    public ApplicantController(ApplicantService applicantService, ApplicationDashboardService applicationDashboardService) {
        this.applicantService = applicantService;
        this.applicationDashboardService = applicationDashboardService;
    }

    @GetMapping("/{userId}/{applicationId}/question/{questionId}")
    public RestResult<ApplicantQuestionResource> getQuestion(@PathVariable("userId") final Long userId,
                                                                    @PathVariable("applicationId") final Long applicationId,
                                                                    @PathVariable("questionId") final Long questionId) {
        return applicantService.getQuestion(userId, questionId, applicationId).toGetResponse();
    }

    @GetMapping("/{userId}/{applicationId}/section/{sectionId}")
    public RestResult<ApplicantSectionResource> getSection(@PathVariable("userId") final Long userId,
                                                           @PathVariable("applicationId") final Long applicationId,
                                                           @PathVariable("sectionId") final Long sectionId) {
        return applicantService.getSection(userId, sectionId, applicationId).toGetResponse();
    }

    @GetMapping("/{userId}/applications/dashboard" )
    public RestResult<ApplicantDashboardResource> getApplicantDashboard(@PathVariable("userId") final Long userId) {
        return applicationDashboardService.getApplicantDashboard(userId).toGetResponse();
    }

}
