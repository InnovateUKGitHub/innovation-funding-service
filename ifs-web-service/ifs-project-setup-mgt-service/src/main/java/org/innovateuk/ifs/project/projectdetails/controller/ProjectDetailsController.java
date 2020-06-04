package org.innovateuk.ifs.project.projectdetails.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.financereviewer.service.FinanceReviewerRestService;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDetailsStartDateForm;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDurationForm;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsStartDateViewModel;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DURATION_MUST_BE_MINIMUM_ONE_MONTH;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.toField;

/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project")
public class ProjectDetailsController {

    private static final String FORM_ATTR_NAME = "form";

    private ProjectService projectService;
    private CompetitionRestService competitionRestService;
    private ProjectDetailsService projectDetailsService;
    private PartnerOrganisationRestService partnerOrganisationRestService;
    private OrganisationRestService organisationRestService;
    private FinanceReviewerRestService financeReviewerRestService;

    public ProjectDetailsController() {
    }

    @Autowired
    public ProjectDetailsController(ProjectService projectService, CompetitionRestService competitionRestService,
                                    ProjectDetailsService projectDetailsService,
                                    PartnerOrganisationRestService partnerOrganisationRestService,
                                    OrganisationRestService organisationRestService,
                                    FinanceReviewerRestService financeReviewerRestService) {
        this.projectService = projectService;
        this.competitionRestService = competitionRestService;
        this.projectDetailsService = projectDetailsService;
        this.partnerOrganisationRestService = partnerOrganisationRestService;
        this.organisationRestService = organisationRestService;
        this.financeReviewerRestService = financeReviewerRestService;
    }

    private static final Log LOG = LogFactory.getLog(ProjectDetailsController.class);

    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder', 'external_finance')")
    @SecuredBySpring(value = "VIEW_PROJECT_DETAILS", description = "Project finance, comp admin, support, innovation lead and stakeholders can view the project details")
    @GetMapping("/{projectId}/details")
    public String viewProjectDetails(@PathVariable("competitionId") final Long competitionId,
                                     @PathVariable("projectId") final Long projectId, Model model,
                                     UserResource loggedInUser,
                                     boolean isSpendProfileGenerated) {

        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource leadOrganisationResource = projectService.getLeadOrganisation(projectId);

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        boolean locationPerPartnerRequired = competitionResource.isLocationPerPartner();

        Optional<SimpleUserResource> financeReviewer = Optional.ofNullable(projectResource.getFinanceReviewer())
                .map(id -> financeReviewerRestService.findFinanceReviewerForProject(projectId).getSuccess());

        List<PartnerOrganisationResource> partnerOrganisations = locationPerPartnerRequired?
                partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess()
                : Collections.emptyList();
        List<OrganisationResource> organisations = partnerOrganisations.stream()
                .map(p -> organisationRestService.getOrganisationById(p.getOrganisation()).getSuccess())
                .collect(Collectors.toList());

        model.addAttribute("model", new ProjectDetailsViewModel(projectResource,
                competitionId,
                competitionResource.getName(),
                loggedInUser,
                leadOrganisationResource.getName(),
                locationPerPartnerRequired,
                partnerOrganisations,
                organisations,
                financeReviewer.map(SimpleUserResource::getName).orElse(null),
                financeReviewer.map(SimpleUserResource::getEmail).orElse(null),
                isSpendProfileGenerated));

        return "project/detail";
    }

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "VIEW_START_DATE", description = "Only the IFS Administrator can view the page to edit the project start date")
    @GetMapping("/{projectId}/details/start-date")
    public String viewStartDate(@PathVariable("projectId") final long projectId, Model model,
                                @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ProjectDetailsStartDateForm form,
                                UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        LocalDate defaultStartDate = projectResource.getTargetStartDate().withDayOfMonth(1);
        form.setProjectStartDate(defaultStartDate);
        return doViewProjectStartDate(model, projectResource, form);
    }

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "UPDATE_START_DATE", description = "Only the IFS Administrator can update the project start date")
    @PostMapping("/{projectId}/details/start-date")
    public String updateStartDate(@PathVariable("competitionId") final long competitionId,
                                  @PathVariable("projectId") final long projectId,
                                  @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsStartDateForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                  Model model,
                                  UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewProjectStartDate(model, projectService.getById(projectId), form);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectDetailsService.updateProjectStartDate(projectId, form.getProjectStartDate());

            return validationHandler.addAnyErrors(updateResult, toField("projectStartDate")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId, competitionId));
        });
    }

    private String doViewProjectStartDate(Model model, ProjectResource projectResource, ProjectDetailsStartDateForm form) {
        model.addAttribute("model", new ProjectDetailsStartDateViewModel(projectResource));
        model.addAttribute(FORM_ATTR_NAME, form);
        return "project/details-start-date";
    }

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "VIEW_EDIT_PROJECT_DURATION", description = "Only the project finance can view the page to edit the project duration")
    @GetMapping("/{projectId}/duration")
    public String viewEditProjectDuration(@PathVariable("projectId") final long projectId, Model model,
                                          UserResource loggedInUser) {


        ProjectDurationForm form = new ProjectDurationForm();
        return doViewEditProjectDuration(projectId, model, form);
    }

    private String doViewEditProjectDuration(long projectId, Model model, ProjectDurationForm form) {

        ProjectResource project = projectService.getById(projectId);

        model.addAttribute("model", ProjectDetailsViewModel.editDurationViewModel(project));
        model.addAttribute(FORM_ATTR_NAME, form);

        return "project/edit-duration";

    }

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE_PROJECT_DURATION", description = "Only the project finance can update the project duration")
    @PostMapping("/{projectId}/duration")
    public String updateProjectDuration(@PathVariable("projectId") final long projectId,
                                        @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDurationForm form,
                                        @SuppressWarnings("unused") BindingResult bindingResult,
                                        ValidationHandler validationHandler,
                                        Model model,
                                        UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewEditProjectDuration(projectId, model, form);

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        validateDuration(form.getDurationInMonths(), validationHandler);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectDetailsService.updateProjectDuration(projectId, Long.parseLong(form.getDurationInMonths()));

            return validationHandler.addAnyErrors(updateResult, toField("durationInMonths")).failNowOrSucceedWith(failureView, successView);
        });
    }

    private void validateDuration(String durationInMonths, ValidationHandler validationHandler) {

        if (StringUtils.isBlank(durationInMonths)) {
            validationHandler.addAnyErrors(serviceFailure(new Error("validation.field.must.not.be.blank", HttpStatus.BAD_REQUEST)), toField("durationInMonths"));
            return;
        }

        if (!StringUtils.isNumeric(durationInMonths)) {
            validationHandler.addAnyErrors(serviceFailure(new Error("validation.standard.integer.non.decimal.format", HttpStatus.BAD_REQUEST)), toField("durationInMonths"));
            return;
        }

        if (Long.parseLong(durationInMonths) < 1) {
            validationHandler.addAnyErrors(serviceFailure(PROJECT_SETUP_PROJECT_DURATION_MUST_BE_MINIMUM_ONE_MONTH), toField("durationInMonths"));
        }
    }

    private String redirectToProjectDetails(long projectId, long competitionId) {
        return "redirect:/competition/" + competitionId + "/project/" + projectId + "/details";
    }
}
