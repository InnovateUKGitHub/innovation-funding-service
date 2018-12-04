package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ProjectInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.AddressLookupBaseController;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectdetails.form.FinanceContactForm;
import org.innovateuk.ifs.project.projectdetails.form.PartnerProjectLocationForm;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDetailsStartDateForm;
import org.innovateuk.ifs.project.projectdetails.form.ProjectManagerForm;
import org.innovateuk.ifs.project.projectdetails.viewmodel.*;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.status.populator.SetupStatusViewModelPopulator;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionAccessibilityHelper;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectUserInviteStatus.EXISTING;
import static org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectUserInviteStatus.PENDING;
import static org.innovateuk.ifs.user.resource.Role.PARTNER;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/project")
public class ProjectDetailsController extends AddressLookupBaseController {
    private static final String SAVE_FC = "save-fc";
    private static final String INVITE_FC = "invite-fc";
    private static final String SAVE_PM = "save-pm";
    private static final String INVITE_PM = "invite-pm";
    private static final String RESEND_FC_INVITE = "resend-fc-invite";
    private static final String RESEND_PM_INVITE = "resend-pm-invite";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProjectDetailsService projectDetailsService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationService;

    @Autowired
    private SetupStatusViewModelPopulator setupStatusViewModelPopulator;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId','ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/details")
    public String viewProjectDetails(@PathVariable("projectId") final Long projectId, Model model,
                                     UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();
        boolean partnerProjectLocationRequired = competitionResource.isLocationPerPartner();

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        List<OrganisationResource> organisations
                = new PrioritySorting<>(getPartnerOrganisations(projectUsers), leadOrganisation, OrganisationResource::getName).unwrap();

        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());
        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(teamStatus);
        boolean spendProfileGenerated = statusAccessor.isSpendProfileGenerated();
        boolean monitoringOfficerAssigned = statusAccessor.isMonitoringOfficerAssigned();

        boolean allProjectDetailsFinanceContactsAndProjectLocationsAssigned = setupStatusViewModelPopulator.checkLeadPartnerProjectDetailsProcessCompleted(teamStatus, partnerProjectLocationRequired);

        model.addAttribute("model", new ProjectDetailsViewModel(projectResource, loggedInUser,
                getUsersPartnerOrganisations(loggedInUser, projectUsers),
                organisations,
                partnerProjectLocationRequired ? partnerOrganisationService.getProjectPartnerOrganisations(projectId).getSuccess()
                        : Collections.emptyList(),
                leadOrganisation, applicationResource, projectUsers, competitionResource,
                projectService.isUserLeadPartner(projectId, loggedInUser.getId()), allProjectDetailsFinanceContactsAndProjectLocationsAssigned,
                getProjectManager(projectResource.getId()).orElse(null), monitoringOfficerAssigned, spendProfileGenerated, statusAccessor.isGrantOfferLetterGenerated(), false));

        return "project/detail";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/readonly")
    public String viewProjectDetailsInReadOnly(@PathVariable("projectId") final Long projectId, Model model,
                                               UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        List<OrganisationResource> organisations
                = new PrioritySorting<>(getPartnerOrganisations(projectUsers), leadOrganisation, OrganisationResource::getName).unwrap();

        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());
        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(teamStatus);
        boolean spendProfileGenerated = statusAccessor.isSpendProfileGenerated();
        boolean monitoringOfficerAssigned = statusAccessor.isMonitoringOfficerAssigned();

        model.addAttribute("model", new ProjectDetailsViewModel(projectResource, loggedInUser,
                getUsersPartnerOrganisations(loggedInUser, projectUsers),
                organisations,
                competitionResource.isLocationPerPartner() ? partnerOrganisationService.getProjectPartnerOrganisations(projectId).getSuccess()
                        : Collections.emptyList(),
                leadOrganisation, applicationResource, projectUsers, competitionResource,
                projectService.isUserLeadPartner(projectId, loggedInUser.getId()), true,
                getProjectManager(projectResource.getId()).orElse(null), monitoringOfficerAssigned, spendProfileGenerated, true, true));

        return "project/detail";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CONTACT_PAGE')")
    @GetMapping("/{projectId}/details/finance-contact")
    public String viewFinanceContact(@PathVariable("projectId") final Long projectId,
                                     @RequestParam(value = "organisation", required = false) Long organisation,
                                     Model model,
                                     @ModelAttribute(name = FORM_ATTR_NAME, binding = false) FinanceContactForm financeContactForm,
                                     UserResource loggedInUser) {
        return doViewFinanceContact(model, projectId, organisation, loggedInUser, financeContactForm, true, false);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CONTACT_PAGE')")
    @PostMapping(value = "/{projectId}/details/finance-contact", params = SAVE_FC)
    public String updateFinanceContact(@PathVariable("projectId") final Long projectId,
                                       Model model,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) FinanceContactForm financeContactForm,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewFinanceContact(model, projectId, financeContactForm.getOrganisation(), loggedInUser, financeContactForm, false, false);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = projectDetailsService.updateFinanceContact(new ProjectOrganisationCompositeId(projectId, financeContactForm.getOrganisation()), financeContactForm.getFinanceContact());

            return validationHandler.addAnyErrors(updateResult, toField("financeContact")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PARTNER_PROJECT_LOCATION_PAGE')")
    @GetMapping("/{projectId}/organisation/{organisationId}/partner-project-location")
    public String viewPartnerProjectLocation(@PathVariable("projectId") final long projectId,
                                             @PathVariable("organisationId") final long organisationId,
                                             Model model,
                                             UserResource loggedInUser) {

        PartnerOrganisationResource partnerOrganisation = partnerOrganisationService.getPartnerOrganisation(projectId, organisationId).getSuccess();
        PartnerProjectLocationForm form = new PartnerProjectLocationForm(partnerOrganisation.getPostcode());

        return doViewPartnerProjectLocation(projectId, organisationId, loggedInUser, model, form);

    }

    private String doViewPartnerProjectLocation(long projectId, long organisationId, UserResource loggedInUser, Model model, PartnerProjectLocationForm form) {

        if (!projectService.userIsPartnerInOrganisationForProject(projectId, organisationId, loggedInUser.getId())) {
            return redirectToProjectDetails(projectId);
        }

        ProjectResource projectResource = projectService.getById(projectId);

        model.addAttribute("model", new PartnerProjectLocationViewModel(projectId, projectResource.getName(), organisationId));
        model.addAttribute(FORM_ATTR_NAME, form);

        return "project/partner-project-location";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PARTNER_PROJECT_LOCATION_PAGE')")
    @PostMapping("/{projectId}/organisation/{organisationId}/partner-project-location")
    public String updatePartnerProjectLocation(@PathVariable("projectId") final long projectId,
                                               @PathVariable("organisationId") final long organisationId,
                                               @ModelAttribute(FORM_ATTR_NAME) PartnerProjectLocationForm form,
                                               @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                               Model model,
                                               UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewPartnerProjectLocation(projectId, organisationId, loggedInUser, model, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectDetailsService.updatePartnerProjectLocation(projectId, organisationId, form.getPostcode());

            return validationHandler.addAnyErrors(updateResult, toField("postcode")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CONTACT_PAGE')")
    @PostMapping(value = "/{projectId}/details/finance-contact", params = INVITE_FC)
    public String inviteFinanceContact(Model model, @PathVariable("projectId") final Long projectId,
                                       @RequestParam(value = "organisation") Long organisation,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) FinanceContactForm financeContactForm,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       UserResource loggedInUser
    ) {

        Supplier<String> failureView = () -> doViewFinanceContact(model, projectId, organisation, loggedInUser, financeContactForm, false, true);
        Supplier<String> successView = () -> redirectToFinanceContact(projectId, organisation);

        return sendInvite(financeContactForm.getName(), financeContactForm.getInviteEmail(), loggedInUser, validationHandler,
                failureView, successView, projectId, organisation,
                (project, projectInviteResource) -> projectDetailsService.inviteFinanceContact(project, projectInviteResource));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CONTACT_PAGE')")
    @PostMapping(value = "/{projectId}/details/finance-contact", params = RESEND_FC_INVITE)
    public String resendFinanceContactInvite(@PathVariable("projectId") final Long projectId,
                                             @RequestParam(value = "organisation") final Long organisation,
                                             @RequestParam(RESEND_FC_INVITE) Long inviteId
    ) {
        resendInvite(inviteId, projectId, (project, projectInviteResource) -> projectDetailsService.inviteFinanceContact(project, projectInviteResource));
        return redirectToFinanceContact(projectId, organisation);
    }

    private void resendInvite(Long id, Long projectId, BiFunction<Long, ProjectInviteResource, ServiceResult<Void>> sendInvite) {

        Optional<ProjectInviteResource> existingInvite = projectDetailsService
                .getInvitesByProject(projectId)
                .getSuccess()
                .stream()
                .filter(i -> id.equals(i.getId()))
                .findFirst();

        existingInvite
                .ifPresent(i -> sendInvite.apply(projectId, existingInvite.get()));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_MANAGER_PAGE')")
    @PostMapping(value = "/{projectId}/details/project-manager", params = INVITE_PM)
    public String inviteProjectManager(Model model,
                                       @PathVariable("projectId") final Long projectId,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectManagerForm projectManagerForm,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       UserResource loggedInUser) {
        populateOriginalProjectManagerForm(projectId, projectManagerForm);

        Supplier<String> failureView = () -> doViewProjectManager(model, projectId, loggedInUser, true);
        Supplier<String> successView = () -> redirectToProjectManager(projectId);

        Long organisation = projectService.getLeadOrganisation(projectId).getId();

        return sendInvite(projectManagerForm.getName(), projectManagerForm.getInviteEmail(), loggedInUser, validationHandler,
                failureView, successView, projectId, organisation,
                (project, projectInviteResource) -> projectDetailsService.inviteProjectManager(project, projectInviteResource));
    }

    private String sendInvite(String inviteName, String inviteEmail, UserResource loggedInUser, ValidationHandler validationHandler,
                              Supplier<String> failureView, Supplier<String> successView, Long projectId, Long organisation,
                              BiFunction<Long, ProjectInviteResource, ServiceResult<Void>> sendInvite) {

        validateIfTryingToInviteSelf(loggedInUser.getEmail(), inviteEmail, validationHandler);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ProjectInviteResource invite = createProjectInviteResourceForNewContact(projectId, inviteName, inviteEmail, organisation);

            ServiceResult<Void> saveResult = projectDetailsService.saveProjectInvite(invite);

            return validationHandler.addAnyErrors(saveResult, asGlobalErrors()).failNowOrSucceedWith(failureView, () -> {

                Optional<ProjectInviteResource> savedInvite = getSavedInvite(projectId, invite);

                if (savedInvite.isPresent()) {
                    ServiceResult<Void> inviteResult = sendInvite.apply(projectId, savedInvite.get());
                    return validationHandler.addAnyErrors(inviteResult).failNowOrSucceedWith(failureView, successView);
                } else {
                    return validationHandler.failNowOrSucceedWith(failureView, successView);
                }
            });
        });
    }

    private void validateIfTryingToInviteSelf(String loggedInUserEmail, String inviteEmail,
                                              ValidationHandler validationHandler) {
        if (equalsIgnoreCase(loggedInUserEmail, inviteEmail)) {
            validationHandler.addAnyErrors(serviceFailure(CommonFailureKeys.PROJECT_SETUP_CANNOT_INVITE_SELF));
        }
    }

    private Optional<ProjectInviteResource> getSavedInvite(Long projectId, ProjectInviteResource invite) {

        return projectDetailsService.getInvitesByProject(projectId).getSuccess().stream()
                .filter(i -> i.getEmail().equals(invite.getEmail())).findFirst();
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_MANAGER_PAGE')")
    @PostMapping(value = "/{projectId}/details/project-manager", params = RESEND_PM_INVITE)
    public String resendProjectManagerInvite(@PathVariable("projectId") final Long projectId,
                                             @RequestParam(RESEND_PM_INVITE) Long userId
    ) {
        resendInvite(userId, projectId, (project, projectInviteResource) -> projectDetailsService.inviteProjectManager(project, projectInviteResource));
        return redirectToProjectManager(projectId);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_MANAGER_PAGE')")
    @GetMapping("/{projectId}/details/project-manager")
    public String viewProjectManager(@PathVariable("projectId") final Long projectId, Model model,
                                     @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ProjectManagerForm projectManagerForm,
                                     UserResource loggedInUser) {

        populateOriginalProjectManagerForm(projectId, projectManagerForm);
        return doViewProjectManager(model, projectId, loggedInUser, false);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_MANAGER_PAGE')")
    @PostMapping(value = "/{projectId}/details/project-manager", params = SAVE_PM)
    public String updateProjectManager(@PathVariable("projectId") final Long projectId, Model model,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectManagerForm projectManagerForm,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       UserResource loggedInUser) {
        Supplier<String> failureView = () -> doViewProjectManager(model, projectId, loggedInUser, false);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectDetailsService.updateProjectManager(projectId, projectManagerForm.getProjectManager());

            return validationHandler.addAnyErrors(updateResult, toField("projectManager")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_START_DATE_PAGE')")
    @GetMapping("/{projectId}/details/start-date")
    public String viewStartDate(@PathVariable("projectId") final Long projectId, Model model,
                                @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ProjectDetailsStartDateForm form,
                                UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        LocalDate defaultStartDate = projectResource.getTargetStartDate().withDayOfMonth(1);
        form.setProjectStartDate(defaultStartDate);
        return doViewProjectStartDate(model, projectResource, form);

    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_START_DATE_PAGE')")
    @PostMapping("/{projectId}/details/start-date")
    public String updateStartDate(@PathVariable("projectId") final Long projectId,
                                  @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsStartDateForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                  Model model,
                                  UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewProjectStartDate(model, projectService.getById(projectId), form);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectDetailsService.updateProjectStartDate(projectId, form.getProjectStartDate());

            return validationHandler.addAnyErrors(updateResult, toField("projectStartDate")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));
        });
    }


    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CONTACT_PAGE')")
    @GetMapping(value = "/{projectId}/details/finance-contact/confirm", params = RESEND_FC_INVITE)
    public String viewResendFinanceContactInviteConfirmPage(@PathVariable("projectId") final Long projectId,
                                                            @RequestParam(RESEND_FC_INVITE) Long inviteId,
                                                            @RequestParam("organisation") Long organisationId,
                                                            Model model) {
        ResendProjectInviteViewModel viewModel = new ResendProjectInviteViewModel(projectId, inviteId, organisationId);
        model.addAttribute("model", viewModel);
        return "project/resend-fc-invite-confirm";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_MANAGER_PAGE')")
    @GetMapping(value = "/{projectId}/details/project-manager/confirm", params = RESEND_PM_INVITE)
    public String viewResendProjectManagerInviteConfirmPage(@PathVariable("projectId") final Long projectId,
                                                            @RequestParam(RESEND_PM_INVITE) Long inviteId,
                                                            Model model) {
        ResendProjectInviteViewModel viewModel = new ResendProjectInviteViewModel(projectId, inviteId);
        model.addAttribute("model", viewModel);
        return "project/resend-pm-invite-confirm";
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

        if (organisation == null) {
            return redirectToProjectDetails(projectId);
        }

        if (!projectService.userIsPartnerInOrganisationForProject(projectId, organisation, loggedInUser.getId())) {
            return redirectToProjectDetails(projectId);
        }

        if (!anyUsersInGivenOrganisationForProject(projectId, organisation)) {
            return redirectToProjectDetails(projectId);
        }

        return modelForFinanceContact(model, projectId, organisation, loggedInUser, form, setDefaultFinanceContact, inviteAction);
    }

    private Optional<ProjectUserResource> getProjectManager(Long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getId() == pu.getRole());
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
        List<ProjectUserInviteModel> invitedUsers = projectDetailsService.getInvitesByProject(projectId).getSuccess().stream()
                .filter(invite -> leadOrganisation.getId().equals(invite.getOrganisation()) && invite.getStatus() != InviteStatus.OPENED)
                .map(invite -> new ProjectUserInviteModel(PENDING, invite.getName() + " (Pending)", invite.getId()))
                .collect(toList());

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();

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

        List<ProjectInviteResource> projectInviteResourceList =
                projectDetailsService.getInvitesByProject(projectId).getSuccess();

        Function<ProjectUserResource, ProjectUserInviteModel> financeContactModelMappingFn =
                user -> new ProjectUserInviteModel(EXISTING, user.getUserName(), user.getUser());

        Function<ProjectInviteResource, ProjectUserInviteModel> inviteeMappingFn = invite ->
                new ProjectUserInviteModel(PENDING, invite.getName() + " (Pending)", invite.getId());

        Predicate<ProjectInviteResource> projectInviteResourceFilterFn = invite ->
                financeContactForm.getOrganisation().equals(invite.getOrganisation()) &&
                        invite.getStatus() != InviteStatus.OPENED;

        List<ProjectUserInviteModel> thisOrganisationUsers = simpleMap(organisationProjectUsers, financeContactModelMappingFn);
        List<ProjectInviteResource> projectInviteResources = simpleFilter(projectInviteResourceList, projectInviteResourceFilterFn);
        List<ProjectUserInviteModel> invitedUsers = simpleMap(projectInviteResources, inviteeMappingFn);

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();

        SelectFinanceContactViewModel viewModel = new SelectFinanceContactViewModel(thisOrganisationUsers, invitedUsers, financeContactForm.getOrganisation(), projectResource, loggedInUser.getId(), applicationResource, competitionResource, inviteAction);

        model.addAttribute(FORM_ATTR_NAME, financeContactForm);
        model.addAttribute("model", viewModel);
        return "project/finance-contact";
    }

    private String doViewProjectManager(Model model, Long projectId, UserResource loggedInUser, boolean inviteAction) {

        ProjectResource projectResource = projectService.getById(projectId);

        if (!projectService.isUserLeadPartner(projectResource.getId(), loggedInUser.getId())) {
            return redirectToProjectDetails(projectId);
        }

        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        populateProjectManagerModel(model, projectId, applicationResource, loggedInUser, inviteAction);

        return "project/project-manager";
    }

    private List<OrganisationResource> getPartnerOrganisations(final List<ProjectUserResource> projectRoles) {

        final Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);

        final Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        SortedSet<OrganisationResource> organisationSet = projectRoles.stream()
                .filter(uar -> uar.getRole() == PARTNER.getId())
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccess())
                .collect(Collectors.toCollection(supplier));

        return new ArrayList<>(organisationSet);
    }

    private List<Long> getUsersPartnerOrganisations(UserResource loggedInUser, List<ProjectUserResource> projectUsers) {
        List<ProjectUserResource> partnerProjectUsers = simpleFilter(projectUsers,
                user -> loggedInUser.getId().equals(user.getUser()) && user.getRoleName().equals(PARTNER.getName()));
        return simpleMap(partnerProjectUsers, ProjectUserResource::getOrganisation);
    }

    private ProjectInviteResource createProjectInviteResourceForNewContact(Long projectId, String name,
                                                                           String email, Long organisationId) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        OrganisationResource organisationResource = organisationRestService.getOrganisationById(organisationId).getSuccess();

        ProjectInviteResource inviteResource = new ProjectInviteResource();

        inviteResource.setProject(projectId);
        inviteResource.setName(name);
        inviteResource.setEmail(email);
        inviteResource.setOrganisation(organisationId);
        inviteResource.setOrganisationName(organisationResource.getName());
        inviteResource.setApplicationId(projectResource.getApplication());
        inviteResource.setLeadOrganisationId(leadOrganisation.getId());

        return inviteResource;
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }

    private String redirectToFinanceContact(long projectId, long organisationId) {
        return "redirect:/project/" + projectId + "/details/finance-contact?organisation=" + organisationId;
    }

    private String redirectToProjectManager(long projectId) {
        return "redirect:/project/" + projectId + "/details/project-manager";
    }
}
