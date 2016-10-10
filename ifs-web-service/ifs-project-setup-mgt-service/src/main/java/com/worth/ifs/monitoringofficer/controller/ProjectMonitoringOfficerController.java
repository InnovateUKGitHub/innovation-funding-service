package com.worth.ifs.monitoringofficer.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.monitoringofficer.form.ProjectMonitoringOfficerForm;
import com.worth.ifs.monitoringofficer.viewmodel.ProjectMonitoringOfficerViewModel;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static com.worth.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller will handle the management of the Monitoring Officer on projects
 */
@Controller
@RequestMapping("/project/{projectId}/monitoring-officer")
public class ProjectMonitoringOfficerController {

    private static final String FORM_ATTR_NAME = "form";

	@Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired


    private CompetitionService competitionService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_MONITORING_OFFICER_SECTION')")
    @RequestMapping(method = GET)
    public String viewMonitoringOfficer(Model model, @PathVariable("projectId") final Long projectId,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Optional<MonitoringOfficerResource> existingMonitoringOfficer = projectService.getMonitoringOfficerForProject(projectId);
        ProjectMonitoringOfficerForm form = new ProjectMonitoringOfficerForm(existingMonitoringOfficer);
        return viewMonitoringOfficer(model, projectId, form, existingMonitoringOfficer.isPresent());
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_MONITORING_OFFICER_SECTION')")
    @RequestMapping(value = "/edit", method = GET)
    public String editMonitoringOfficer(Model model, @PathVariable("projectId") final Long projectId,
                                        @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Optional<MonitoringOfficerResource> existingMonitoringOfficer = projectService.getMonitoringOfficerForProject(projectId);
        ProjectMonitoringOfficerForm form = new ProjectMonitoringOfficerForm(existingMonitoringOfficer);
        return editMonitoringOfficer(model, projectId, form, existingMonitoringOfficer.isPresent());
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_MONITORING_OFFICER_SECTION')")
    @RequestMapping(value = "/confirm", method = POST)
    public String confirmMonitoringOfficerDetails(Model model,
                                                  @PathVariable("projectId") final Long projectId,
                                                  @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectMonitoringOfficerForm form,
                                                  @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                                  @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Supplier<String> failureView = () -> editMonitoringOfficer(model, projectId, form, false);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            doViewMonitoringOfficer(model, projectId, form, false, false);
            return "project/monitoring-officer-confirm";
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_MONITORING_OFFICER_SECTION')")
    @RequestMapping(value = "/assign", method = POST)
    public String updateMonitoringOfficerDetails(Model model,
                                                 @PathVariable("projectId") final Long projectId,
                                                 @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectMonitoringOfficerForm form,
                                                 @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                                 @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Supplier<String> failureView = () -> editMonitoringOfficer(model, projectId, form, false);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectService.updateMonitoringOfficer(projectId, form.getFirstName(),
                    form.getLastName(), form.getEmailAddress(), form.getPhoneNumber());

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> redirectToMonitoringOfficerViewTemporarily(projectId));
        });
    }

    private void checkInCorrectStateToUseMonitoringOfficerPage(Long projectId) {
        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId, Optional.empty());

        if (!COMPLETE.equals(teamStatus.getLeadPartnerStatus().getProjectDetailsStatus())) {
            throw new ForbiddenActionException("Unable to assign Monitoring Officers until the Project Details have been submitted");
        }
    }

    private String viewMonitoringOfficer(Model model, Long projectId, ProjectMonitoringOfficerForm form, boolean existingMonitoringOfficerAssigned) {
        return doViewMonitoringOfficer(model, projectId, form, false, existingMonitoringOfficerAssigned);
    }

    private String editMonitoringOfficer(Model model, Long projectId, ProjectMonitoringOfficerForm form, boolean existingMonitoringOfficerAssigned) {
        return doViewMonitoringOfficer(model, projectId, form, true, existingMonitoringOfficerAssigned);
    }

    private String doViewMonitoringOfficer(Model model, Long projectId, ProjectMonitoringOfficerForm form, boolean currentlyEditing, boolean existingMonitoringOfficer) {

        boolean editMode = currentlyEditing || !existingMonitoringOfficer;

        ProjectMonitoringOfficerViewModel viewModel = populateMonitoringOfficerViewModel(projectId, editMode, existingMonitoringOfficer);
        model.addAttribute("model", viewModel);
        model.addAttribute(FORM_ATTR_NAME, form);

        return "project/monitoring-officer";
    }

    private ProjectMonitoringOfficerViewModel populateMonitoringOfficerViewModel(Long projectId, boolean editMode, boolean existingMonitoringOfficer) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(projectResource.getApplication());
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
        String projectManagerName = getProjectManagerName(projectResource);
        List<String> partnerOrganisationNames = getPartnerOrganisationNames(projectId);
        String innovationArea = competition.getInnovationAreaName();

        return new ProjectMonitoringOfficerViewModel(projectId, projectResource.getName(),
                innovationArea, projectResource.getAddress(), projectResource.getTargetStartDate(), projectManagerName,
                partnerOrganisationNames, competitionSummary, existingMonitoringOfficer, editMode);
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

    private List<String> getPartnerOrganisationNames(Long projectId) {
        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        return simpleMap(partnerOrganisations, OrganisationResource::getName);
    }
}
