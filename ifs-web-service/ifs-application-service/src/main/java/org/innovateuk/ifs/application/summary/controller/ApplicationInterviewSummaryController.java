package org.innovateuk.ifs.application.summary.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryOrigin;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.summary.populator.ApplicationInterviewSummaryViewModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all requests that are related to the application summary for an assessor.
 */
@Controller
@SecuredBySpring(value="Controller", description = "Assessor can view an application summary for interview panel", securedType = ApplicationInterviewSummaryController.class)
@RequestMapping("/application")
public class ApplicationInterviewSummaryController {

    private ApplicationInterviewSummaryViewModelPopulator applicationInterviewSummaryViewModelPopulator;
    private ApplicationService applicationService;


    public ApplicationInterviewSummaryController() {
    }

    @Autowired
    public ApplicationInterviewSummaryController(ApplicationInterviewSummaryViewModelPopulator applicationInterviewSummaryViewModelPopulator,
                                                 ApplicationService applicationService) {
        this.applicationInterviewSummaryViewModelPopulator = applicationInterviewSummaryViewModelPopulator;
        this.applicationService = applicationService;
    }

    @SecuredBySpring(value = "READ", description = "Assessors and Comp exec users have permission to view the application summary page for an interview panel")
    @PreAuthorize("hasAnyAuthority('assessor', 'comp_admin', 'project_finance', 'innovation_lead')")
    @GetMapping("/{applicationId}/interview-summary")
    public String applicationSummary(@ModelAttribute("form") ApplicationForm form,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user,
                                     @RequestParam(value = "origin", defaultValue = "ASSESSOR_INTERVIEW") String origin,
                                     @RequestParam MultiValueMap<String, String> queryParams) {

        if(userIsInternal(user.getRoles())){
            origin = "COMP_EXEC_INTERVIEW";
        }

        String backUrl = buildBackUrl(origin, applicationId, queryParams);

        model.addAttribute("applicationInterviewSummaryViewModel", applicationInterviewSummaryViewModelPopulator.populate(applicationId, user, backUrl, origin));
        return "application-interview-summary";
    }

    private String buildBackUrl(String origin, long applicationId, MultiValueMap<String, String> queryParams) {

        ApplicationResource application = applicationService.getById(applicationId);
        long competitionId = application.getCompetition();

        String baseUrl = ApplicationSummaryOrigin.valueOf(origin).getOriginUrl();
        queryParams.remove("origin");

        return UriComponentsBuilder.fromPath(baseUrl)
                .queryParams(queryParams)
                .buildAndExpand(asMap( "competitionId", competitionId))
                .encode()
                .toUriString();
    }

    private boolean userIsInternal(List<Role> userroles){
        for (Role ur : userroles) {
            if(Role.internalRoles().contains(ur)){
                return true;
            }
        }
        return false;
    }

}
