package org.innovateuk.ifs.project.spendprofile.controller;

import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.CaseInsensitiveConverter;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.form.ProjectSpendProfileApprovalForm;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.project.spendprofile.viewmodel.ProjectSpendProfileApprovalViewModel;
import org.innovateuk.ifs.spendprofile.OrganisationReviewDetails;
import org.innovateuk.ifs.spendprofile.SpendProfileService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.resource.Authority.*;

/**
 * This Controller handles Spend Profile activity for the Internal Competition team members
 */
@Controller
@RequestMapping("/project/{projectId}/spend-profile")
public class ProjectSpendProfileApprovalController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private SpendProfileService spendProfileService;

    @Value("${ifs.monitoringofficer.spendprofile.update.enabled}")
    private Boolean isMOSpendProfileUpdateEnabled;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ApprovalType.class, new CaseInsensitiveConverter<>(ApprovalType.class));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_SPEND_PROFILE_SECTION')")
    @GetMapping("/approval")
    public String viewSpendProfileApproval(@P("projectId")@PathVariable Long projectId, Model model, UserResource loggedInUser) {
        return doViewSpendProfileApproval(projectId, model, loggedInUser);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_SPEND_PROFILE_SECTION')")
    @PostMapping("/approval/{approvalType}")
    public String saveSpendProfileApproval(@P("projectId")@PathVariable Long projectId,
                                           @PathVariable ApprovalType approvalType,
                                           @ModelAttribute ProjectSpendProfileApprovalForm form,
                                           Model model,
                                           UserResource loggedInUser,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewSpendProfileApproval(projectId, model , loggedInUser);
        ServiceResult<Void> generateResult = spendProfileService.approveOrRejectSpendProfile(projectId, approvalType);

        return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () ->
                redirectToCompetitionSummaryPage(projectId)
        );
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'APPROVE_REJECT_SPEND_PROFILE')")
    @PostMapping("/approval/submit")
    public String submitSpendProfileApproval(@P("projectId")@PathVariable Long projectId,
                                           @ModelAttribute ProjectSpendProfileApprovalForm form,
                                           Model model,
                                           UserResource loggedInUser,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewSpendProfileApproval(projectId, model , loggedInUser);
        ApprovalType approvalType = form.isSpendProfileApproved() ? ApprovalType.APPROVED : ApprovalType.REJECTED;
        ServiceResult<Void> generateResult = spendProfileService.approveOrRejectSpendProfile(projectId, approvalType);

        return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () ->
                redirectToCompetitionSummaryPage(projectId)
        );
    }

    private String doViewSpendProfileApproval(Long projectId, Model model, UserResource loggedInUser) {
        ProjectSpendProfileApprovalViewModel viewModel = populateSpendProfileApprovalViewModel(projectId, loggedInUser);

        model.addAttribute("model", viewModel);

        return "project/finance/spend-profile/approval";
    }

    private ProjectSpendProfileApprovalViewModel populateSpendProfileApprovalViewModel(Long projectId, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        CompetitionSummaryResource competitionSummary = applicationSummaryRestService.getCompetitionSummary(project.getCompetition()).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        UserResource user = userRestService.retrieveUserById(competition.getLeadTechnologist()).getSuccess();
        String leadTechnologist = competition.getLeadTechnologist() != null ? user.getName() : "";
        ApprovalType approvalType = spendProfileService.getSpendProfileStatusByProjectId(projectId);
        boolean isReadOnly = userCannotApproveOrReject(loggedInUser);

        final OrganisationResource leadOrganisation = projectService.getLeadOrganisation(project.getId());

        List<OrganisationResource> organisations = new PrioritySorting<>(projectService.getPartnerOrganisationsForProject(project.getId()),
                leadOrganisation, OrganisationResource::getName).unwrap();

        Map<Long, OrganisationReviewDetails> editablePartners = getOrganisationReviewDetails(project.getId(), organisations, loggedInUser);

        List<OrganisationResource> organisationResources = projectService.getPartnerOrganisationsForProject(projectId);

        return new ProjectSpendProfileApprovalViewModel(competitionSummary,
                                                        leadTechnologist,
                                                        approvalType,
                                                        organisationResources,
                                                        project,
                                                        editablePartners,
                                                        leadOrganisation,
                                                        isMOSpendProfileUpdateEnabled,
                                                        isReadOnly);
    }

    private Map<Long, OrganisationReviewDetails> getOrganisationReviewDetails(final Long projectId, List<OrganisationResource> partnerOrganisations, final UserResource loggedInUser) {
        return partnerOrganisations.stream().collect(Collectors.toMap(OrganisationResource::getId,
                o -> { Optional<SpendProfileResource> spendProfileResource = spendProfileService.getSpendProfile(projectId, o.getId());
                    return new OrganisationReviewDetails(o.getId(),
                            o.getName(),
                            spendProfileResource.map(SpendProfileResource::isMarkedAsComplete).orElse(false),
                            false,
                            true,
                            spendProfileResource.map(SpendProfileResource::getReviewedBy).orElse(null),
                            spendProfileResource.map(SpendProfileResource::getReviewedOn).orElse(null));
                }, (v1, v2) -> v1, LinkedHashMap::new));
    }

    private String redirectToCompetitionSummaryPage(Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        return "redirect:/competition/" + project.getCompetition() + "/status";
    }

    private boolean userCannotApproveOrReject(UserResource loggedInUser) {
        return isMOSpendProfileUpdateEnabled ?
                (!loggedInUser.hasAuthority(IFS_ADMINISTRATOR) &&
                        loggedInUser.hasAnyAuthority(asList(AUDITOR, COMP_ADMIN, PROJECT_FINANCE)))
                : loggedInUser.hasAuthority(AUDITOR);
       }
}
