package com.worth.ifs.dashboard;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationStatusRestService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;

/**
 * This controller will handle requests related to the current applicant. So pages that are relative to that user,
 * are implemented here. For example the my-applications page.
 */
@Controller
@RequestMapping("/applicant")
public class ApplicantController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationStatusRestService applicationStatusService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private CompetitionService competitionService;

    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard(Model model, HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);

        model.addAttribute("applicationProgress", applicationService.getProgress(user.getId()));

        List<ApplicationResource> inProgress = applicationService.getInProgress(user.getId());
        List<ApplicationResource> finished = applicationService.getFinished(user.getId());

        List<ApplicationResource> projectsInSetup = projectsInSetup(finished);
        
        Map<Long, CompetitionResource> competitions = createCompetitionMap(inProgress, finished);
        Map<Long, ApplicationStatusResource> applicationStatusMap = createApplicationStatusMap(inProgress, finished);

        model.addAttribute("applicationsInProcess", inProgress);
        model.addAttribute("applicationsAssigned", getAssignedApplications(inProgress, user));
        model.addAttribute("applicationsFinished", finished);
        model.addAttribute("projectsInSetup", projectsInSetup);
        model.addAttribute("competitions", competitions);
        model.addAttribute("applicationStatuses", applicationStatusMap);

        return "applicant-dashboard";
    }

    private List<ApplicationResource> projectsInSetup(List<ApplicationResource> finished) {
		return simpleFilter(finished, a -> ApplicationStatusConstants.APPROVED.getId().equals(a.getApplicationStatus()));
	}

	/**
     * Get a list of application ids, where one of the questions is assigned to the current user. This is only for the
     * collaborators, since the leadapplicant is the default assignee.
     */
    private List<Long> getAssignedApplications(List<ApplicationResource> inProgress, UserResource user){
        return inProgress.stream().filter(applicationResource -> {
                    ProcessRoleResource role = processRoleService.findProcessRole(user.getId(), applicationResource.getId());
                    if(!UserRoleType.LEADAPPLICANT.getName().equals(role.getRoleName())) {
                        int count = applicationService.getAssignedQuestionsCount(applicationResource.getId(), role.getId());
                        return count == 0 ? false : true;
                    }else{
                        return false;
                    }
                }
        ).mapToLong(applicationResource -> applicationResource.getId()).boxed().collect(Collectors.toList());
    }

    // TODO DW - INFUND-1555 - handle rest result
    private Map<Long, ApplicationStatusResource> createApplicationStatusMap(List<ApplicationResource>... resources){
        return combineLists(resources).stream()
            .collect(
                Collectors.toMap(
                    ApplicationResource::getId,
                    application -> applicationStatusService.getApplicationStatusById(application.getApplicationStatus()).getSuccessObjectOrThrowException()
                )
            );
    }

    private Map<Long, CompetitionResource> createCompetitionMap(List<ApplicationResource>... resources){
        return combineLists(resources).stream()
            .collect(
                Collectors.toMap(
                    ApplicationResource::getId,
                    application -> competitionService.getById(application.getCompetition())
                )
            );
    }

}
