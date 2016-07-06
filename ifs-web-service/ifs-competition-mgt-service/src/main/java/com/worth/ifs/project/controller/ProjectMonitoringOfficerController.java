package com.worth.ifs.project.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.BindingResultTarget;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.controller.form.ProjectMonitoringOfficerForm;
import com.worth.ifs.project.controller.viewmodel.ProjectMonitoringOfficerViewModel;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
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

import static com.worth.ifs.controller.RestFailuresToValidationErrorBindingUtils.bindAnyErrorsToField;
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

    static final String FORM_ATTR_NAME = "form";

	@Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private ProcessRoleService processRoleService;

    @RequestMapping(method = GET)
    public String viewMonitoringOfficer(Model model, @PathVariable("projectId") final Long projectId,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);
        return viewMonitoringOfficerWithNewFormInViewMode(model, projectId);
    }

    @RequestMapping(value = "/edit", method = GET)
    public String editMonitoringOfficer(Model model, @PathVariable("projectId") final Long projectId,
                                        @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);
        return viewMonitoringOfficerWithNewFormInEditMode(model, projectId);
    }

    @RequestMapping(value = "/confirm", method = POST)
    public String confirmMonitoringOfficerDetails(Model model,
                                       @PathVariable("projectId") final Long projectId,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectMonitoringOfficerForm form,
                                       BindingResult bindingResult,
                                       @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Supplier<String> failureView = () -> viewMonitoringOfficerWithExistingForm(model, projectId, form);

        if (bindingResult.hasErrors()) {
            form.setBindingResult(bindingResult);
            return failureView.get();
        }

        doViewMonitoringOfficer(model, projectId, form, false, false);
        return "project/monitoring-officer-confirm";
    }

    @RequestMapping(value = "/assign", method = POST)
    public String updateMonitoringOfficerDetails(Model model,
                                                 @PathVariable("projectId") final Long projectId,
                                                 @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectMonitoringOfficerForm form,
                                                 BindingResult bindingResult,
                                                 @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        checkInCorrectStateToUseMonitoringOfficerPage(projectId);

        Supplier<String> failureView = () -> viewMonitoringOfficerWithExistingForm(model, projectId, form);

        if (bindingResult.hasErrors()) {
            form.setBindingResult(bindingResult);
            return failureView.get();
        }

        ServiceResult<Void> updateResult = projectService.updateMonitoringOfficer(projectId, form.getFirstName(), form.getLastName(), form.getEmailAddress(), form.getPhoneNumber());
        return handleErrorsOrRedirectToMonitoringOfficerViewTemporarily("", projectId, model, form, bindingResult, updateResult, failureView);
    }

    private void checkInCorrectStateToUseMonitoringOfficerPage(@PathVariable("projectId") Long projectId) {
        ProjectResource project = projectService.getById(projectId);

        if (!project.isProjectDetailsSubmitted()) {
            throw new ForbiddenActionException("Unable to assign Monitoring Officers until the Project Details have been submitted");
        }
    }

    private String viewMonitoringOfficerWithNewFormInViewMode(Model model, Long projectId) {
        return doViewMonitoringOfficer(model, projectId, false);
    }

    private String viewMonitoringOfficerWithNewFormInEditMode(Model model, Long projectId) {
        return doViewMonitoringOfficer(model, projectId, true);
    }

    private String viewMonitoringOfficerWithExistingForm(Model model, @PathVariable("projectId") Long projectId, @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectMonitoringOfficerForm form) {
        return doViewMonitoringOfficer(model, projectId, form, true, false);
    }

    private String doViewMonitoringOfficer(Model model, Long projectId, boolean editMode) {
        Optional<MonitoringOfficerResource> existingMonitoringOfficer = projectService.getMonitoringOfficerForProject(projectId);
        ProjectMonitoringOfficerForm form = new ProjectMonitoringOfficerForm(existingMonitoringOfficer);
        return doViewMonitoringOfficer(model, projectId, form, editMode, existingMonitoringOfficer.isPresent());
    }

    private String doViewMonitoringOfficer(Model model, Long projectId, ProjectMonitoringOfficerForm form, boolean editMode, boolean existingMonitoringOfficer) {

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
    private String handleErrorsOrRedirectToMonitoringOfficerViewTemporarily(
            String fieldName, long projectId, Model model,
            BindingResultTarget form, BindingResult bindingResult,
            ServiceResult<?> result,
            Supplier<String> viewSupplier) {

        if (result.isFailure()) {
            bindAnyErrorsToField(result, fieldName, bindingResult, form);
            model.addAttribute(FORM_ATTR_NAME, form);
            return viewSupplier.get();
        }

        return redirectToMonitoringOfficerViewTemporarily(projectId);
    }

    /**
     * "Temporarily" because the final target page to redirect to after submission has not yet been built
     */
    private String redirectToMonitoringOfficerViewTemporarily(long projectId) {
        return "redirect:/project/" + projectId + "/monitoring-officer";
    }

    private String getProjectManagerName(ProjectResource projectResource) {

        Long projectManagerId = projectResource.getProjectManager();

        if (projectManagerId == null) {
            return "";
        }

        // TODO DW - Project Manager needs to be a ProjectUser, not a ProcessRole
        List<ProcessRoleResource> projectUsers = processRoleService.findProcessRolesByApplicationId(projectResource.getApplication());
        Optional<ProcessRoleResource> projectManager = simpleFindFirst(projectUsers, pu -> projectManagerId.equals(pu.getId()));
        return projectManager.map(pu -> pu.getUserName()).orElse("");

        //        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        //        Optional<ProjectUserResource> projectManager = simpleFindFirst(projectUsers, pu -> projectManagerId.equals(pu.getId()));
        //        return projectManager.map(pu -> pu.getUserName()).orElse("");
    }

    private List<String> getPartnerOrganisationNames(Long projectId) {
        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        return simpleMap(partnerOrganisations, OrganisationResource::getName);
    }
}
