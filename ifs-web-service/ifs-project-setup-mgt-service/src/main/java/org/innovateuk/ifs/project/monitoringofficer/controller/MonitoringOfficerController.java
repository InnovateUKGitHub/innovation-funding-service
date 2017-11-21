package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.monitoringofficer.MonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerForm;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.StatusService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.joining;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;

/**
 * This controller will handle the management of the Monitoring Officer on projects
 */
@Controller
@RequestMapping("/project/{projectId}/monitoring-officer")
public class MonitoringOfficerController {

    private static final String FORM_ATTR_NAME = "form";

	@Autowired
    private ProjectService projectService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private MonitoringOfficerService monitoringOfficerService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_MONITORING_OFFICER_SECTION')")
    @GetMapping
    public String viewMonitoringOfficer(Model model, @P("projectId")@PathVariable("projectId") final Long projectId,
                                UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Optional<MonitoringOfficerResource> existingMonitoringOfficer = monitoringOfficerService.getMonitoringOfficerForProject(projectId);
        MonitoringOfficerForm form = new MonitoringOfficerForm(existingMonitoringOfficer);
        return viewMonitoringOfficer(model, projectId, form, existingMonitoringOfficer.isPresent(), isInternalAdmin(loggedInUser));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'EDIT_MONITORING_OFFICER_SECTION')")
    @GetMapping("/edit")
    public String editMonitoringOfficer(Model model, @P("projectId")@PathVariable("projectId") final Long projectId,
                                        UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Optional<MonitoringOfficerResource> existingMonitoringOfficer = monitoringOfficerService.getMonitoringOfficerForProject(projectId);
        MonitoringOfficerForm form = new MonitoringOfficerForm(existingMonitoringOfficer);
        return editMonitoringOfficer(model, projectId, form, existingMonitoringOfficer.isPresent(), isInternalAdmin(loggedInUser));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'EDIT_MONITORING_OFFICER_SECTION')")
    @PostMapping("/confirm")
    public String confirmMonitoringOfficerDetails(Model model,
                                                  @P("projectId") @PathVariable("projectId") final Long projectId,
                                                  @Valid @ModelAttribute(FORM_ATTR_NAME) MonitoringOfficerForm form,
                                                  @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                                  UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Supplier<String> failureView = () -> editMonitoringOfficer(model, projectId, form, false, isInternalAdmin(loggedInUser));

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            doViewMonitoringOfficer(model, projectId, form, false, false, isInternalAdmin(loggedInUser));
            return "project/monitoring-officer-confirm";
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'EDIT_MONITORING_OFFICER_SECTION')")
    @PostMapping("/assign")
    public String updateMonitoringOfficerDetails(Model model,
                                                 @P("projectId")@PathVariable("projectId") final Long projectId,
                                                 @Valid @ModelAttribute(FORM_ATTR_NAME) MonitoringOfficerForm form,
                                                 @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                                 UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Supplier<String> failureView = () -> editMonitoringOfficer(model, projectId, form, false, isInternalAdmin(loggedInUser));

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = monitoringOfficerService.updateMonitoringOfficer(projectId, form.getFirstName(),
                    form.getLastName(), form.getEmailAddress(), form.getPhoneNumber());

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> redirectToMonitoringOfficerViewTemporarily(projectId));
        });
    }

    private void checkInCorrectStateToUseMonitoringOfficerPage(Long projectId) {
        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());

        if (!COMPLETE.equals(teamStatus.getLeadPartnerStatus().getProjectDetailsStatus())) {
            throw new ForbiddenActionException("Unable to assign Monitoring Officers until the Project Details have been submitted");
        }
    }

    private String viewMonitoringOfficer(Model model, Long projectId, MonitoringOfficerForm form, boolean existingMonitoringOfficerAssigned, boolean editable) {
        return doViewMonitoringOfficer(model, projectId, form, false, existingMonitoringOfficerAssigned, editable);
    }

    private String editMonitoringOfficer(Model model, Long projectId, MonitoringOfficerForm form, boolean existingMonitoringOfficerAssigned, boolean editable) {
        return doViewMonitoringOfficer(model, projectId, form, true, existingMonitoringOfficerAssigned, editable);
    }

    private String doViewMonitoringOfficer(Model model, Long projectId, MonitoringOfficerForm form, boolean currentlyEditing, boolean existingMonitoringOfficer, boolean editable) {
        boolean editMode = currentlyEditing || !existingMonitoringOfficer;

        MonitoringOfficerViewModel viewModel = populateMonitoringOfficerViewModel(projectId, editMode, existingMonitoringOfficer, editable);
        model.addAttribute("model", viewModel);
        model.addAttribute(FORM_ATTR_NAME, form);

        return "project/monitoring-officer";
    }

    private MonitoringOfficerViewModel populateMonitoringOfficerViewModel(Long projectId, boolean editMode, boolean existingMonitoringOfficer, boolean editable) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(projectResource.getApplication());
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        CompetitionSummaryResource competitionSummary = applicationSummaryRestService.getCompetitionSummary(application.getCompetition()).getSuccessObjectOrThrowException();
        String projectManagerName = getProjectManagerName(projectResource);

        final OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        final List<String> partnerOrganisationNames = simpleMap(new PrioritySorting<>(projectService.getPartnerOrganisationsForProject(projectId),
                        leadOrganisation, OrganisationResource::getName).unwrap(), OrganisationResource::getName);

        String innovationAreas = competition.getInnovationAreaNames().stream().collect(joining(", "));

        return new MonitoringOfficerViewModel(projectId, projectResource.getName(),
                innovationAreas, projectResource.getAddress(), projectResource.getTargetStartDate(), projectManagerName,
                partnerOrganisationNames, leadOrganisation.getName(), competitionSummary, existingMonitoringOfficer, editMode, editable);
    }

    /**
     * "Temporarily" because the final target page to redirect to after submission has not yet been built
     */
    private String redirectToMonitoringOfficerViewTemporarily(long projectId) {
        return "redirect:/project/" + projectId + "/monitoring-officer";
    }

    private String getProjectManagerName(ProjectResource project) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(project.getId());
        Optional<ProjectUserResource> projectManager = simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getName().equals(pu.getRoleName()));
        return projectManager.map(ProjectUserResource::getUserName).orElse("");
    }
}
