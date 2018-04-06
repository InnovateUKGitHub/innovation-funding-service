package org.innovateuk.ifs.project.projectdetails.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DURATION_MUST_BE_MINIMUM_ONE_MONTH;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.user.resource.Role.PARTNER;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project")
public class ProjectDetailsController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDetailsService projectDetailsService;

    @Autowired
    private OrganisationService organisationService;

    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead')")
    @SecuredBySpring(value = "VIEW_PROJECT_DETAILS", description = "Project finance, comp admin, support and innovation lead can view the project details")
    @GetMapping("/{projectId}/details")
    public String viewProjectDetails(@PathVariable("competitionId") final Long competitionId,
                                     @PathVariable("projectId") final Long projectId, Model model,
                                     UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        OrganisationResource leadOrganisationResource = projectService.getLeadOrganisation(projectId);

        List<OrganisationResource> partnerOrganisations = sortedOrganisations(getPartnerOrganisations(projectUsers), leadOrganisationResource);

        model.addAttribute("model", new ProjectDetailsViewModel(projectResource,
                competitionId,
                null,
                leadOrganisationResource.getName(),
                getProjectManager(projectUsers).orElse(null),
                getFinanceContactForPartnerOrganisation(projectUsers, partnerOrganisations)));

        return "project/detail";
    }

    private List<OrganisationResource> getPartnerOrganisations(final List<ProjectUserResource> projectRoles) {
        return  projectRoles.stream()
                .filter(uar -> uar.getRole() == PARTNER.getId())
                .map(uar -> organisationService.getOrganisationById(uar.getOrganisation()))
                .collect(Collectors.toList());
    }

    private List<OrganisationResource> sortedOrganisations(List<OrganisationResource> organisations,
                                                           OrganisationResource lead)
    {
        return new PrioritySorting<>(organisations, lead, OrganisationResource::getName).unwrap();
    }

    private Optional<ProjectUserResource> getProjectManager(List<ProjectUserResource> projectUsers) {
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getId() == pu.getRole());
    }

    private Map<OrganisationResource, ProjectUserResource> getFinanceContactForPartnerOrganisation(List<ProjectUserResource> projectUsers, List<OrganisationResource> partnerOrganisations) {
        List<ProjectUserResource> financeRoles = simpleFilter(projectUsers, ProjectUserResource::isFinanceContact);

        Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap = new LinkedHashMap<>();

        partnerOrganisations.stream().forEach(organisation ->
                organisationFinanceContactMap.put(organisation,
                        simpleFindFirst(financeRoles, financeUserResource -> financeUserResource.getOrganisation().equals(organisation.getId())).orElse(null))
        );

        return organisationFinanceContactMap;
    }

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "VIEW_EDIT_PROJECT_DURATION", description = "Only the project finance can view the page to edit the project duration")
    @GetMapping("/{projectId}/edit-duration")
    public String editProjectDuration(@PathVariable("competitionId") final long competitionId,
                                      @PathVariable("projectId") final long projectId, Model model,
                                      UserResource loggedInUser) {

        return doViewEditProjectDuration(competitionId, projectId, model);

/*        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionService.getById(competitionId);

        model.addAttribute("model", new ProjectDetailsViewModel(project,
                competitionId,
                competition.getName(),
                null,
                null,
                null));

        return "project/edit-duration";*/
    }

    private String doViewEditProjectDuration(long competitionId, long projectId, Model model) {

        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionService.getById(competitionId);

        model.addAttribute("model", new ProjectDetailsViewModel(project,
                competitionId,
                competition.getName(),
                null,
                null,
                null));

        return "project/edit-duration";

    }

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE_PROJECT_DURATION", description = "Only the project finance can update the project duration")
    @PostMapping("/{projectId}/update-duration")
    public String updateProjectDuration(@PathVariable("competitionId") final long competitionId,
                                        @PathVariable("projectId") final long projectId,
                                        @ModelAttribute("durationInMonths")  final String durationInMonths,
                                        @SuppressWarnings("unused") BindingResult bindingResult,
                                        ValidationHandler validationHandler,
                                        Model model,
                                        UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewEditProjectDuration(competitionId, projectId, model);

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        validateDuration(durationInMonths, validationHandler);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectDetailsService.updateProjectDuration(projectId, Long.parseLong(durationInMonths));

            return validationHandler.addAnyErrors(updateResult, asGlobalErrors()).failNowOrSucceedWith(failureView, successView);
        });
    }

    private boolean validateDuration(String durationInMonths, ValidationHandler validationHandler) {

        if (StringUtils.isBlank(durationInMonths) || !StringUtils.isNumeric(durationInMonths)) {
            validationHandler.addAnyErrors(serviceFailure(CommonFailureKeys.GENERAL_INVALID_ARGUMENT), asGlobalErrors());
            return false;
        }

        if (Long.parseLong(durationInMonths) < 1) {
            validationHandler.addAnyErrors(serviceFailure(CommonFailureKeys.PROJECT_SETUP_PROJECT_DURATION_MUST_BE_MINIMUM_ONE_MONTH), asGlobalErrors());
            return false;
        }
        return true;
    }
}
