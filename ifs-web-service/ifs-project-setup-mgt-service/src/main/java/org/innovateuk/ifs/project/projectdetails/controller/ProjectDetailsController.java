package org.innovateuk.ifs.project.projectdetails.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
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
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DURATION_MUST_BE_GREATER_THAN_OR_EQUAL_TO_MAX_EXISTING_MILESTONE;
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
    private ProjectProcurementMilestoneRestService projectProcurementMilestoneRestService;

    public ProjectDetailsController() {
    }

    @Autowired
    public ProjectDetailsController(ProjectService projectService, CompetitionRestService competitionRestService,
                                    ProjectDetailsService projectDetailsService,
                                    PartnerOrganisationRestService partnerOrganisationRestService,
                                    OrganisationRestService organisationRestService,
                                    FinanceReviewerRestService financeReviewerRestService,
                                    ProjectProcurementMilestoneRestService projectProcurementMilestoneRestService) {
        this.projectService = projectService;
        this.competitionRestService = competitionRestService;
        this.projectDetailsService = projectDetailsService;
        this.partnerOrganisationRestService = partnerOrganisationRestService;
        this.organisationRestService = organisationRestService;
        this.financeReviewerRestService = financeReviewerRestService;
        this.projectProcurementMilestoneRestService = projectProcurementMilestoneRestService;
    }

    private static final Log LOG = LogFactory.getLog(ProjectDetailsController.class);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'support', 'innovation_lead', 'stakeholder', 'external_finance')")
    @SecuredBySpring(value = "VIEW_PROJECT_DETAILS", description = "Project finance, comp admin, support, innovation lead and stakeholders can view the project details")
    @GetMapping("/{projectId}/details")
    public String viewProjectDetails(@PathVariable("competitionId") final Long competitionId,
                                     @PathVariable("projectId") final Long projectId, Model model,
                                     @RequestParam(required = false, defaultValue = "false") boolean displayFinanceReviewerSuccess,
                                     @RequestParam(required = false, defaultValue = "false") boolean resumedFromOnHold,
                                     UserResource loggedInUser,
                                     boolean isSpendProfileGenerated) {

        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource leadOrganisationResource = projectService.getLeadOrganisation(projectId);

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        Optional<SimpleUserResource> financeReviewer = Optional.ofNullable(projectResource.getFinanceReviewer())
                .map(id -> financeReviewerRestService.findFinanceReviewerForProject(projectId).getSuccess());

        List<PartnerOrganisationResource> partnerOrganisations =  partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();
        List<OrganisationResource> organisations = partnerOrganisations.stream()
                .map(p -> organisationRestService.getOrganisationById(p.getOrganisation()).getSuccess())
                .collect(Collectors.toList());

        model.addAttribute("displayFinanceReviewerSuccess", displayFinanceReviewerSuccess);
        model.addAttribute("resumedFromOnHold", resumedFromOnHold);
        model.addAttribute("model", new ProjectDetailsViewModel(projectResource,
                competitionId,
                competitionResource.getName(),
                loggedInUser,
                leadOrganisationResource,
                partnerOrganisations,
                organisations,
                financeReviewer.map(SimpleUserResource::getName).orElse(null),
                financeReviewer.map(SimpleUserResource::getEmail).orElse(null),
                isSpendProfileGenerated,
                competitionResource.isKtp()));

        return "project/detail";
    }

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "VIEW_START_DATE", description = "Only the IFS Administrator can view the page to edit the project start date")
    @GetMapping("/{projectId}/details/start-date")
    public String viewStartDate(@PathVariable("projectId") final long projectId, Model model,
                                @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ProjectDetailsStartDateForm form,
                                UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();
        LocalDate defaultStartDate = projectResource.getTargetStartDate().withDayOfMonth(1);
        form.setProjectStartDate(defaultStartDate);
        return doViewProjectStartDate(model, projectResource, form, competitionResource);
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

        ProjectResource projectResource = projectService.getById(projectId);
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();

        if (competitionResource.isKtp()) {
            LocalDate defaultKtpStartDate = TimeZoneUtil.toUkTimeZone(competitionResource.getEndDate()).plusMonths(12).toLocalDate();
            form.setProjectStartDate(defaultKtpStartDate.withDayOfMonth(1));
        }

        Supplier<String> failureView = () -> doViewProjectStartDate(model, projectResource, form, competitionResource);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectDetailsService.updateProjectStartDate(projectId, form.getProjectStartDate());

            return validationHandler.addAnyErrors(updateResult, toField("projectStartDate")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId, competitionId));
        });
    }

    private String doViewProjectStartDate(Model model, ProjectResource projectResource,
                                          ProjectDetailsStartDateForm form, CompetitionResource competitionResource) {
        model.addAttribute("model", new ProjectDetailsStartDateViewModel(projectResource, competitionResource));
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
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();

        model.addAttribute("model", ProjectDetailsViewModel.editDurationViewModel(project, competitionResource.isKtp()));
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

        validateDuration(projectId, form.getDurationInMonths(), validationHandler);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectDetailsService.updateProjectDuration(projectId, Long.parseLong(form.getDurationInMonths()));

            return validationHandler.addAnyErrors(updateResult, toField("durationInMonths")).failNowOrSucceedWith(failureView, successView);
        });
    }

    private void validateDuration(long projectId, String durationInMonths, ValidationHandler validationHandler) {

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

        RestResult<List<ProjectProcurementMilestoneResource>> milestonesResult = projectProcurementMilestoneRestService.getByProjectId(projectId);

        if (noMilestonesFound(milestonesResult)) {
            return;
        }

        List<ProjectProcurementMilestoneResource> milestones = milestonesResult.getSuccess();
        Optional<Integer> maxMilestoneMonth = milestones.stream().map(ProjectProcurementMilestoneResource::getMonth).max(Comparator.naturalOrder());

        if (maxMilestoneMonth.isPresent() && (Integer.parseInt(durationInMonths) < maxMilestoneMonth.get())) {
            validationHandler.addAnyErrors(serviceFailure(new Error(PROJECT_SETUP_PROJECT_DURATION_MUST_BE_GREATER_THAN_OR_EQUAL_TO_MAX_EXISTING_MILESTONE, HttpStatus.BAD_REQUEST)), toField("durationInMonths"));
        }
    }

    private boolean noMilestonesFound(RestResult<List<ProjectProcurementMilestoneResource>> milestonesResult) {
        return milestonesResult.isFailure() && milestonesResult.getStatusCode() == HttpStatus.NOT_FOUND;
    }

    private String redirectToProjectDetails(long projectId, long competitionId) {
        return "redirect:/competition/" + competitionId + "/project/" + projectId + "/details";
    }
}
