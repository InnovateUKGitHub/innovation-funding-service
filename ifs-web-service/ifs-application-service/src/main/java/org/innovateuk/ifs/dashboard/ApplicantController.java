package org.innovateuk.ifs.dashboard;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.ApplicationStatusRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

/**
 * This controller will handle requests related to the current applicant. So pages that are relative to that user,
 * are implemented here. For example the my-applications page.
 */
@Controller
@RequestMapping("/applicant")
@PreAuthorize("hasAuthority('applicant')")
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

    @Autowired
    private ProjectService projectService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);

        model.addAttribute("applicationProgress", applicationService.getProgress(user.getId()));

        List<ApplicationResource> inProgress = applicationService.getInProgress(user.getId());
        List<ApplicationResource> finished = applicationService.getFinished(user.getId());

        List<ProjectResource> projectsInSetup = projectService.findByUser(user.getId()).getSuccessObject();

        List<ApplicationResource> applicationsForProjectsInSetup = getApplicationsForProjectsInSetup(projectsInSetup);

        Map<Long, CompetitionResource> competitions = createCompetitionMap(inProgress, finished, applicationsForProjectsInSetup);
        Map<Long, ApplicationStatusResource> applicationStatusMap = createApplicationStatusMap(inProgress, finished);

        model.addAttribute("applicationsInProgress", inProgress);
        model.addAttribute("applicationsAssigned", getAssignedApplications(inProgress, user));
        model.addAttribute("applicationsFinished", finished);
        model.addAttribute("projectsInSetup", projectsInSetup);
        model.addAttribute("competitions", competitions);
        model.addAttribute("applicationStatuses", applicationStatusMap);

        return "applicant-dashboard";
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
                        return count != 0;
                    }else{
                        return false;
                    }
                }
        ).mapToLong(ApplicationResource::getId).boxed().collect(Collectors.toList());
    }

    // TODO DW - INFUND-1555 - handle rest result
    @SafeVarargs
    private final Map<Long, ApplicationStatusResource> createApplicationStatusMap(List<ApplicationResource>... resources){
        return combineLists(resources).stream()
            .collect(
                Collectors.toMap(
                    ApplicationResource::getId,
                    application -> applicationStatusService.getApplicationStatusById(application.getApplicationStatus()).getSuccessObjectOrThrowException()
                )
            );
    }

    @SafeVarargs
    private final Map<Long, CompetitionResource> createCompetitionMap(List<ApplicationResource>... resources){
        return combineLists(resources).stream()
            .collect(
                Collectors.toMap(
                    ApplicationResource::getId,
                    application -> competitionService.getById(application.getCompetition()), (p1, p2) -> p1)
            );
    }

    private final List<ApplicationResource> getApplicationsForProjectsInSetup(List<ProjectResource> resources){

        return resources.stream().map(project -> applicationService.getById(project.getApplication())).collect(Collectors.toList());
    }
}
