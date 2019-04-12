package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.form.LegacyMonitoringOfficerForm;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.LegacyMonitoringOfficerViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;

/**
 * This controller will handle the management of the Monitoring Officer on projects
 */
@Controller
@RequestMapping("/project/{projectId}/monitoring-officer")
public class LegacyMonitoringOfficerController {

    private static final String FORM_ATTR_NAME = "form";

	@Autowired
    private ProjectService projectService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private MonitoringOfficerRestService monitoringOfficerService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_MONITORING_OFFICER_SECTION')")
    @GetMapping
    public String viewMonitoringOfficer(Model model, @P("projectId")@PathVariable("projectId") final Long projectId,
                                UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Optional<MonitoringOfficerResource> existingMonitoringOfficer = monitoringOfficerService.findMonitoringOfficerForProject(projectId).getOptionalSuccessObject();
        if (!existingMonitoringOfficer.isPresent()) {
            return "redirect:/monitoring-officer/view-all";
        }
        LegacyMonitoringOfficerForm form = new LegacyMonitoringOfficerForm(existingMonitoringOfficer);
        return viewMonitoringOfficer(model, projectId, form, existingMonitoringOfficer.isPresent(), isInternalAdmin(loggedInUser));
    }

    private void checkInCorrectStateToUseMonitoringOfficerPage(Long projectId) {
        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());

        if (!COMPLETE.equals(teamStatus.getLeadPartnerStatus().getProjectDetailsStatus())) {
            throw new ForbiddenActionException("Unable to assign Monitoring Officers until the Project Details have been submitted");
        }
    }

    private String viewMonitoringOfficer(Model model, Long projectId, LegacyMonitoringOfficerForm form, boolean existingMonitoringOfficerAssigned, boolean editable) {
        return doViewMonitoringOfficer(model, projectId, form, existingMonitoringOfficerAssigned, editable);
    }

    private String doViewMonitoringOfficer(Model model, Long projectId, LegacyMonitoringOfficerForm form, boolean existingMonitoringOfficer, boolean editable) {
        boolean editMode = false;

        LegacyMonitoringOfficerViewModel viewModel = populateMonitoringOfficerViewModel(projectId, editMode, existingMonitoringOfficer, editable);
        model.addAttribute("model", viewModel);
        model.addAttribute(FORM_ATTR_NAME, form);

        return "project/monitoring-officer";
    }

    private LegacyMonitoringOfficerViewModel populateMonitoringOfficerViewModel(Long projectId, boolean editMode, boolean existingMonitoringOfficer, boolean editable) {
        ProjectResource projectResource = projectService.getById(projectId);
        Long applicationId = projectResource.getApplication();
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        CompetitionSummaryResource competitionSummary = applicationSummaryRestService.getCompetitionSummary(application.getCompetition()).getSuccess();
        String projectManagerName = getProjectManagerName(projectResource);

        final OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        final List<String> partnerOrganisationNames = simpleMap(new PrioritySorting<>(projectService.getPartnerOrganisationsForProject(projectId),
                        leadOrganisation, OrganisationResource::getName).unwrap(), OrganisationResource::getName);

        String innovationAreas = competition.getInnovationAreaNames().stream().collect(joining(", "));

        return new LegacyMonitoringOfficerViewModel(projectId, projectResource.getName(), applicationId,
                innovationAreas, projectResource.getAddress(), projectResource.getTargetStartDate(), projectManagerName,
                partnerOrganisationNames, leadOrganisation.getName(), competitionSummary, existingMonitoringOfficer, editMode, editable);
    }

    private String getProjectManagerName(ProjectResource project) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(project.getId());
        Optional<ProjectUserResource> projectManager = simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getId() == pu.getRole());
        return projectManager.map(ProjectUserResource::getUserName).orElse("");
    }
}
