package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.service.OrganisationAddressRestService;
import org.innovateuk.ifs.project.AddressLookupBaseController;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectdetails.form.FinanceContactForm;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDetailsAddressForm;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDetailsStartDateForm;
import org.innovateuk.ifs.project.projectdetails.form.ProjectManagerForm;
import org.innovateuk.ifs.project.projectdetails.viewmodel.*;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.sections.ProjectSetupSectionAccessibilityHelper;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectUserInviteStatus.EXISTING;
import static org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectUserInviteStatus.PENDING;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/project")
public class ProjectDetailsController extends AddressLookupBaseController {
    private static final String SAVE_FC = "save_fc";
    private static final String INVITE_FC = "invite_fc";
    private static final String SAVE_PM = "save_pm";
    private static final String INVITE_PM = "invite_pm";

	@Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/details")
    public String viewProjectDetails(@PathVariable("projectId") final Long projectId, Model model,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());

	    List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        List<OrganisationResource> partnerOrganisations
                = new PrioritySorting<>(getPartnerOrganisations(projectUsers), leadOrganisation, OrganisationResource::getName).unwrap();
        boolean isSubmissionAllowed = projectService.isSubmitAllowed(projectId).getSuccessObject();

        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId, Optional.empty());
        ProjectSetupSectionAccessibilityHelper statusAccessor = new ProjectSetupSectionAccessibilityHelper(teamStatus);
        boolean projectDetailsSubmitted = statusAccessor.isProjectDetailsSubmitted();

        model.addAttribute("model", new ProjectDetailsViewModel(projectResource, loggedInUser,
                getUsersPartnerOrganisations(loggedInUser, projectUsers),
                partnerOrganisations, leadOrganisation, applicationResource, projectUsers, competitionResource,
                projectService.isUserLeadPartner(projectId, loggedInUser.getId()), projectDetailsSubmitted,
                getProjectManager(projectResource.getId()).orElse(null), isSubmissionAllowed, false));

        return "project/detail";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/readonly")
    public String viewProjectDetailsInReadOnly(@PathVariable("projectId") final Long projectId, Model model,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        List<OrganisationResource> partnerOrganisations
                = new PrioritySorting<>(getPartnerOrganisations(projectUsers), leadOrganisation, OrganisationResource::getName).unwrap();

        model.addAttribute("model", new ProjectDetailsViewModel(projectResource, loggedInUser,
                getUsersPartnerOrganisations(loggedInUser, projectUsers),
                partnerOrganisations, leadOrganisation, applicationResource, projectUsers, competitionResource,
                projectService.isUserLeadPartner(projectId, loggedInUser.getId()), true,
                getProjectManager(projectResource.getId()).orElse(null), false, true));

        return "project/detail";
    }


    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/confirm-project-details")
    public String projectDetailConfirmSubmit(@PathVariable("projectId") final Long projectId, Model model,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectResource project = projectService.getById(projectId);
        Boolean isSubmissionAllowed = projectService.isSubmitAllowed(projectId).getSuccessObject();

        model.addAttribute("projectId", projectId);
        model.addAttribute("applicationId", project.getApplication());
        model.addAttribute("currentUser", loggedInUser);
        model.addAttribute("isSubmissionAllowed", isSubmissionAllowed);
        return "project/confirm-project-details";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/details/finance-contact")
    public String viewFinanceContact(@PathVariable("projectId") final Long projectId,
                                     @RequestParam(value="organisation",required=false) Long organisation,
                                     Model model,
                                     @ModelAttribute(FORM_ATTR_NAME) FinanceContactForm financeContactForm,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return doViewFinanceContact(model, projectId, organisation, loggedInUser, financeContactForm, true, false);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @PostMapping(value = "/{projectId}/details/finance-contact", params = SAVE_FC)
    public String updateFinanceContact(@PathVariable("projectId") final Long projectId,
                                       Model model,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) FinanceContactForm financeContactForm,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewFinanceContact(model, projectId, financeContactForm.getOrganisation(), loggedInUser, financeContactForm, false, false);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = projectService.updateFinanceContact(new ProjectOrganisationCompositeId(projectId, financeContactForm.getOrganisation()), financeContactForm.getFinanceContact());

            return validationHandler.addAnyErrors(updateResult, toField("financeContact")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @PostMapping(value = "/{projectId}/details/finance-contact", params = INVITE_FC)
    public String inviteFinanceContact(Model model, @PathVariable("projectId") final Long projectId,
                                       @RequestParam(value="organisation") Long organisation,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) FinanceContactForm financeContactForm,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       @ModelAttribute("loggedInUser") UserResource loggedInUser
                                       ) {

        Supplier<String> failureView = () -> doViewFinanceContact(model, projectId, organisation, loggedInUser, financeContactForm, false, true);
        Supplier<String> successView = () -> redirectToFinanceContact(projectId, organisation);

        validateIfTryingToInviteSelf(loggedInUser.getEmail(), financeContactForm.getInviteEmail(), validationHandler);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            InviteProjectResource invite = createProjectInviteResourceForNewContact (projectId, financeContactForm.getName(), financeContactForm.getInviteEmail(), organisation);

            ServiceResult<Void> saveResult = projectService.saveProjectInvite(invite);

            return validationHandler.addAnyErrors(saveResult, asGlobalErrors()).failNowOrSucceedWith(failureView, () -> {

                Optional<InviteProjectResource> savedInvite = projectService.getInvitesByProject(projectId).getSuccessObjectOrThrowException().stream()
                        .filter(i -> i.getEmail().equals(invite.getEmail())).findFirst();

                if(savedInvite.isPresent()) {
                    ServiceResult<Void> inviteResult = projectService.inviteFinanceContact(projectId, savedInvite.get());
                    return validationHandler.addAnyErrors(inviteResult).failNowOrSucceedWith(failureView, successView);
                } else {
                    return validationHandler.failNowOrSucceedWith(failureView, successView);
                }
            });
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/details/project-manager")
    public String viewProjectManager(@PathVariable("projectId") final Long projectId, Model model,
                                     @ModelAttribute(FORM_ATTR_NAME) ProjectManagerForm projectManagerForm,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        populateOriginalProjectManagerForm(projectId, projectManagerForm);
        return doViewProjectManager(model, projectId, loggedInUser, false);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @PostMapping(value = "/{projectId}/details/project-manager", params = SAVE_PM)
    public String updateProjectManager(@PathVariable("projectId") final Long projectId, Model model,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectManagerForm projectManagerForm,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        Supplier<String> failureView = () -> doViewProjectManager(model, projectId, loggedInUser, false);
        
        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectService.updateProjectManager(projectId, projectManagerForm.getProjectManager());

            return validationHandler.addAnyErrors(updateResult, toField("projectManager")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @PostMapping(value = "/{projectId}/details/project-manager", params = INVITE_PM)
    public String inviteProjectManager(Model model,
                                       @PathVariable("projectId") final Long projectId,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectManagerForm projectManagerForm,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
        @ModelAttribute("loggedInUser") UserResource loggedInUser
    ) {
        populateOriginalProjectManagerForm(projectId, projectManagerForm);

        Supplier<String> failureView = () -> doViewProjectManager(model, projectId, loggedInUser, true);
        Supplier<String> successView = () -> redirectToProjectManager(projectId);

        validateIfTryingToInviteSelf(loggedInUser.getEmail(), projectManagerForm.getInviteEmail(), validationHandler);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            Long organisation = projectService.getLeadOrganisation(projectId).getId();
            InviteProjectResource invite = createProjectInviteResourceForNewContact (projectId, projectManagerForm.getName(), projectManagerForm.getInviteEmail(), organisation);

            ServiceResult<Void> saveResult = projectService.saveProjectInvite(invite);

            return validationHandler.addAnyErrors(saveResult, asGlobalErrors()).failNowOrSucceedWith(failureView, () -> {

                Optional<InviteProjectResource> savedInvite = projectService.getInvitesByProject(projectId).getSuccessObjectOrThrowException().stream()
                        .filter(i -> i.getEmail().equals(invite.getEmail())).findFirst();

                if(savedInvite.isPresent()) {
                    ServiceResult<Void> inviteResult = projectService.inviteProjectManager(projectId, savedInvite.get());
                    return validationHandler.addAnyErrors(inviteResult).failNowOrSucceedWith(failureView, successView);
                } else {
                    return validationHandler.failNowOrSucceedWith(failureView, successView);
                }
            });
        });
    }

    private void validateIfTryingToInviteSelf(String loggedInUserEmail, String inviteEmail,
                                              ValidationHandler validationHandler) {
        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(loggedInUserEmail, inviteEmail)) {

            validationHandler.addAnyErrors(serviceFailure(CommonFailureKeys.PROJECT_SETUP_CANNOT_INVITE_SELF));
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/details/start-date")
    public String viewStartDate(@PathVariable("projectId") final Long projectId, Model model,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsStartDateForm form,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        LocalDate defaultStartDate = projectResource.getTargetStartDate().withDayOfMonth(1);
        form.setProjectStartDate(defaultStartDate);
        return doViewProjectStartDate(model, projectResource, form);

    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @PostMapping("/{projectId}/details/start-date")
    public String updateStartDate(@PathVariable("projectId") final Long projectId,
                                  @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsStartDateForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                  Model model,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewProjectStartDate(model, projectService.getById(projectId), form);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectService.updateProjectStartDate(projectId, form.getProjectStartDate());

            return validationHandler.addAnyErrors(updateResult, toField("projectStartDate")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/details/project-address")
    public String viewAddress(@PathVariable("projectId") final Long projectId,
                              Model model,
                              @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form) {

        ProjectResource project = projectService.getById(projectId);
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = loadDataIntoModel(project);
        if(project.getAddress() != null && project.getAddress().getId() != null && project.getAddress().getOrganisations().size() > 0) {
            RestResult<OrganisationAddressResource> result = organisationAddressRestService.findOne(project.getAddress().getOrganisations().get(0));
            if (result.isSuccess()) {
                form.setAddressType(OrganisationAddressType.valueOf(result.getSuccessObject().getAddressType().getName()));
            }
        }
        model.addAttribute("model", projectDetailsAddressViewModel);
        return "project/details-address";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @PostMapping("/{projectId}/details/project-address")
    public String updateAddress(@PathVariable("projectId") final Long projectId,
                                Model model,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form,
                                @SuppressWarnings("unused") BindingResult bindingResult,
                                ValidationHandler validationHandler) {

        ProjectResource projectResource = projectService.getById(projectId);

        if (validationHandler.hasErrors() && form.getAddressType() == null) {
            return viewCurrentAddressForm(model, form, projectResource);
        }

        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectResource.getId());

        AddressResource newAddressResource = null;
        OrganisationAddressType addressType = null;
        switch (form.getAddressType()) {
            case REGISTERED:
            case OPERATING:
            case PROJECT:
                Optional<OrganisationAddressResource> organisationAddressResource = getAddress(leadOrganisation, form.getAddressType());
                if (organisationAddressResource.isPresent()) {
                    newAddressResource = organisationAddressResource.get().getAddress();
                }
                addressType = form.getAddressType();
                break;
            case ADD_NEW:
                form.getAddressForm().setTriedToSave(true);
                newAddressResource = form.getAddressForm().getSelectedPostcode();
                if(newAddressResource == null){
                    addAddressNotProvidedValidationError(bindingResult, validationHandler);
                }

                if (validationHandler.hasErrors()) {
                    return viewCurrentAddressForm(model, form, projectResource);
                }
                addressType = PROJECT;
                break;
            default:
                newAddressResource = null;
                break;
        }

        projectResource.setAddress(newAddressResource);
        ServiceResult<Void> updateResult = projectService.updateAddress(leadOrganisation.getId(), projectId, addressType, newAddressResource);

        return updateResult.handleSuccessOrFailure(
                failure -> {
                    validationHandler.addAnyErrors(failure, asGlobalErrors());
                    return viewAddress(projectId, model, form);
                },
                success -> redirectToProjectDetails(projectId));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @PostMapping(value = "/{projectId}/details/project-address", params = SEARCH_ADDRESS)
    public String searchAddress(@PathVariable("projectId") Long projectId,
                                Model model,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form,
                                BindingResult bindingResult) {
        if(StringUtils.isEmpty(form.getAddressForm().getPostcodeInput())){
            bindingResult.addError(createPostcodeSearchFieldError());
        }
        form.getAddressForm().setSelectedPostcodeIndex(null);
        form.getAddressForm().setTriedToSearch(true);
        form.setAddressType(OrganisationAddressType.valueOf(form.getAddressType().name()));
        ProjectResource project = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, project);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @PostMapping(value = "/{projectId}/details/project-address", params = SELECT_ADDRESS)
    public String selectAddress(@PathVariable("projectId") Long projectId,
                                Model model,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form) {
        form.getAddressForm().setSelectedPostcode(null);
        ProjectResource project = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, project);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @PostMapping(value = "/{projectId}/details/project-address", params = MANUAL_ADDRESS)
    public String manualAddress(@PathVariable("projectId") Long projectId, Model model,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form) {
        AddressForm addressForm = form.getAddressForm();
        addressForm.setManualAddress(true);
        ProjectResource project = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, project);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_PROJECT_DETAILS_SECTION')")
    @PostMapping("/{projectId}/details/submit")
    public String submitProjectDetails(@PathVariable("projectId") Long projectId) {
        projectService.setApplicationDetailsSubmitted(projectId).getSuccessObjectOrThrowException();
        return redirectToProjectDetails(projectId);
    }

    private String doViewProjectStartDate(Model model, ProjectResource projectResource, ProjectDetailsStartDateForm form) {
        model.addAttribute("model", new ProjectDetailsStartDateViewModel(projectResource));
        model.addAttribute(FORM_ATTR_NAME, form);
        return "project/details-start-date";
    }

    private void populateOriginalProjectManagerForm(final Long projectId, ProjectManagerForm projectManagerForm) {
        Optional<ProjectUserResource> existingProjectManager = getProjectManager(projectId);
        projectManagerForm.setProjectManager(existingProjectManager.map(ProjectUserResource::getUser).orElse(null));
    }

    private String doViewFinanceContact(Model model, Long projectId, Long organisation, UserResource loggedInUser, FinanceContactForm form, boolean setDefaultFinanceContact, boolean inviteAction) {

        if(organisation == null) {
            return redirectToProjectDetails(projectId);
        }

        if(!organisationService.userIsPartnerInOrganisationForProject(projectId, organisation, loggedInUser.getId())){
            return redirectToProjectDetails(projectId);
        }

        if(!anyUsersInGivenOrganisationForProject(projectId, organisation)){
            return redirectToProjectDetails(projectId);
        }

        return modelForFinanceContact(model, projectId, organisation, loggedInUser, form, setDefaultFinanceContact, inviteAction);
    }

    private Optional<ProjectUserResource> getProjectManager(Long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getName().equals(pu.getRoleName()));
    }

    private void populateProjectManagerModel(Model model, final Long projectId,
                                             ApplicationResource applicationResource, UserResource loggedInUser,
                                             boolean inviteAction) {

        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);

        List<ProjectUserInviteModel> thisOrganisationUsers = projectService.getLeadPartners(projectId).stream()
            .filter(user -> leadOrganisation.getId().equals(user.getOrganisation()))
            .map(user -> new ProjectUserInviteModel(EXISTING, user.getUserName(), user.getUser()))
            .collect(toList());
        List<ProjectUserInviteModel> invitedUsers = projectService.getInvitesByProject(projectId).getSuccessObjectOrThrowException().stream()
            .filter(invite -> leadOrganisation.getId().equals(invite.getOrganisation()) && invite.getStatus() != InviteStatus.OPENED)
            .map(invite -> new ProjectUserInviteModel(PENDING, invite.getName() + " (Pending)", projectId))
            .collect(toList());

        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());

        SelectProjectManagerViewModel viewModel = new SelectProjectManagerViewModel(thisOrganisationUsers, invitedUsers, projectResource, loggedInUser.getId(), applicationResource, competitionResource, inviteAction);

        model.addAttribute("model", viewModel);
    }

    private boolean anyUsersInGivenOrganisationForProject(Long projectId, Long organisationId) {
        List<ProjectUserResource> thisProjectUsers = projectService.getProjectUsersForProject(projectId);
        List<ProjectUserResource> projectUsersForOrganisation = simpleFilter(thisProjectUsers, user -> user.getOrganisation().equals(organisationId));
        return !projectUsersForOrganisation.isEmpty();
    }

    private String modelForFinanceContact(Model model, Long projectId, Long organisation, UserResource loggedInUser, FinanceContactForm financeContactForm, boolean setDefaultFinanceContact, boolean inviteAction) {

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        List<ProjectUserResource> financeContacts = simpleFilter(projectUsers, pr -> pr.isFinanceContact() && organisation.equals(pr.getOrganisation()));

        financeContactForm.setOrganisation(organisation);

        if (setDefaultFinanceContact && !financeContacts.isEmpty()) {
            financeContactForm.setFinanceContact(getOnlyElement(financeContacts).getUser());
        }

        return modelForFinanceContact(model, projectId, financeContactForm, loggedInUser, inviteAction);  //, organisation
    }

    private String modelForFinanceContact(Model model, Long projectId, FinanceContactForm financeContactForm, UserResource loggedInUser, boolean inviteAction) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());

        List<ProjectUserResource> partnerUsers = projectService.getProjectUsersWithPartnerRole(projectId);

        List<ProjectUserResource> organisationProjectUsers = simpleFilter(partnerUsers, pu ->
                pu.getOrganisation().equals(financeContactForm.getOrganisation()));

        List<InviteProjectResource> inviteProjectResourceList =
                projectService.getInvitesByProject(projectId).getSuccessObjectOrThrowException();

        Function<ProjectUserResource, ProjectUserInviteModel> financeContactModelMappingFn =
                user -> new ProjectUserInviteModel(EXISTING, user.getUserName(), user.getUser());

        Function<InviteProjectResource, ProjectUserInviteModel> inviteeMappingFn = invite ->
                new ProjectUserInviteModel(PENDING, invite.getName() + " (Pending)", invite.getId());

        Predicate<InviteProjectResource> inviteProjectResourceFilterFn = invite ->
                financeContactForm.getOrganisation().equals(invite.getOrganisation()) &&
                invite.getStatus() != InviteStatus.OPENED;

        List<ProjectUserInviteModel> thisOrganisationUsers = simpleMap(organisationProjectUsers, financeContactModelMappingFn);
        List<InviteProjectResource> inviteProjectResources = simpleFilter(inviteProjectResourceList, inviteProjectResourceFilterFn);
        List<ProjectUserInviteModel> invitedUsers = simpleMap(inviteProjectResources, inviteeMappingFn);

        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());

        SelectFinanceContactViewModel viewModel = new SelectFinanceContactViewModel(thisOrganisationUsers, invitedUsers, financeContactForm.getOrganisation(), projectResource, loggedInUser.getId(), applicationResource, competitionResource, inviteAction);

        model.addAttribute(FORM_ATTR_NAME, financeContactForm);
        model.addAttribute("model", viewModel);
        return "project/finance-contact";
    }

    private String doViewProjectManager(Model model, Long projectId, UserResource loggedInUser, boolean inviteAction) {

        ProjectResource projectResource = projectService.getById(projectId);

        if(!projectService.isUserLeadPartner(projectResource.getId(), loggedInUser.getId())) {
            return redirectToProjectDetails(projectId);
        }

        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        populateProjectManagerModel(model, projectId, applicationResource, loggedInUser, inviteAction);

        return "project/project-manager";
    }

    private String viewCurrentAddressForm(Model model, ProjectDetailsAddressForm form,
                                          ProjectResource project){
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = loadDataIntoModel(project);
        processAddressLookupFields(form);
        model.addAttribute("model", projectDetailsAddressViewModel);
        return "project/details-address";
    }

    private ProjectDetailsAddressViewModel loadDataIntoModel(final ProjectResource project){
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = new ProjectDetailsAddressViewModel(project);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(project.getId());

        Optional<OrganisationAddressResource> registeredAddress = getAddress(leadOrganisation, REGISTERED);
        registeredAddress.ifPresent(organisationAddressResource -> projectDetailsAddressViewModel.setRegisteredAddress(organisationAddressResource.getAddress()));

        Optional<OrganisationAddressResource> operatingAddress = getAddress(leadOrganisation, OPERATING);
        operatingAddress.ifPresent(organisationAddressResource -> projectDetailsAddressViewModel.setOperatingAddress(organisationAddressResource.getAddress()));

        Optional<OrganisationAddressResource> projectAddress = getAddress(leadOrganisation, PROJECT);
        projectAddress.ifPresent(organisationAddressResource -> projectDetailsAddressViewModel.setProjectAddress(organisationAddressResource.getAddress()));

        return projectDetailsAddressViewModel;
    }

    private List<OrganisationResource> getPartnerOrganisations(final List<ProjectUserResource> projectRoles) {

        final Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);

        final Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        SortedSet<OrganisationResource> organisationSet = projectRoles.stream()
                .filter(uar -> uar.getRoleName().equals(PARTNER.getName()))
                .map(uar -> organisationService.getOrganisationById(uar.getOrganisation()))
                .collect(Collectors.toCollection(supplier));

        return new ArrayList<>(organisationSet);
    }

    private List<Long> getUsersPartnerOrganisations(UserResource loggedInUser, List<ProjectUserResource> projectUsers) {
        List<ProjectUserResource> partnerProjectUsers = simpleFilter(projectUsers,
                user -> loggedInUser.getId().equals(user.getUser()) && user.getRoleName().equals(PARTNER.getName()));
        return simpleMap(partnerProjectUsers, ProjectUserResource::getOrganisation);
    }

    private InviteProjectResource createProjectInviteResourceForNewContact(Long projectId, String name,
                                                                           String email, Long organisationId) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);

        InviteProjectResource inviteResource = new InviteProjectResource();

        inviteResource.setProject(projectId);
        inviteResource.setName(name);
        inviteResource.setEmail (email);
        inviteResource.setOrganisation(organisationId);
        inviteResource.setOrganisationName(organisationResource.getName());
        inviteResource.setApplicationId(projectResource.getApplication());
        inviteResource.setLeadOrganisationId(leadOrganisation.getId());

        return inviteResource;
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }

    private String redirectToFinanceContact(long projectId, long organisationId){
        return "redirect:/project/" + projectId + "/details/finance-contact?organisation=" + organisationId;
    }

    private String redirectToProjectManager(long projectId){
        return "redirect:/project/" + projectId + "/details/project-manager";
    }
}
