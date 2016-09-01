package com.worth.ifs.project;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.bankdetails.form.ProjectDetailsAddressForm;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.service.BankDetailsRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.organisation.service.OrganisationAddressRestService;
import com.worth.ifs.project.consortiumoverview.viewmodel.ConsortiumPartnerStatus;
import com.worth.ifs.project.consortiumoverview.viewmodel.LeadPartnerModel;
import com.worth.ifs.project.consortiumoverview.viewmodel.ProjectConsortiumStatusViewModel;
import com.worth.ifs.project.consortiumoverview.viewmodel.RegularPartnerModel;
import com.worth.ifs.project.form.FinanceContactForm;
import com.worth.ifs.project.form.ProjectManagerForm;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.viewmodel.ProjectDetailsAddressViewModel;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateForm;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateViewModel;
import com.worth.ifs.project.viewmodel.ProjectDetailsViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.address.resource.OrganisationAddressType.OPERATING;
import static com.worth.ifs.address.resource.OrganisationAddressType.PROJECT;
import static com.worth.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static com.worth.ifs.project.consortiumoverview.viewmodel.ConsortiumPartnerStatus.*;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.worth.ifs.project.form.InviteeForm;
import com.worth.ifs.project.viewmodel.FinanceContactModel;

import com.worth.ifs.project.viewmodel.SelectFinanceContactViewModel;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import static com.worth.ifs.project.viewmodel.FinanceContactStatus.EXISTING;
import static com.worth.ifs.project.viewmodel.FinanceContactStatus.PENDING;

import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.stream.Collectors.toList;
/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/project")
public class ProjectDetailsController extends AddressLookupBaseController {

    private static final String INVITE_FORM_ATTR_NAME = "inviteForm";
	@Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    @Autowired
    private BankDetailsRestService bankDetailsService;

    @RequestMapping(value = "/{projectId}/details", method = RequestMethod.GET)
    public String projectDetail(Model model, @PathVariable("projectId") final Long projectId,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());

	    List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        List<OrganisationResource> partnerOrganisations = getPartnerOrganisations(projectUsers);
        Boolean isSubmissionAllowed = projectService.isSubmitAllowed(projectId).getSuccessObject();

        model.addAttribute("project", projectResource);
        model.addAttribute("currentUser", loggedInUser);
        model.addAttribute("projectManager", getProjectManager(projectResource.getId()).orElse(null));

        model.addAttribute("model", new ProjectDetailsViewModel(projectResource, loggedInUser,
                getUsersPartnerOrganisations(loggedInUser, projectUsers),
                partnerOrganisations, applicationResource, projectUsers, competitionResource,
                projectService.isUserLeadPartner(projectId, loggedInUser.getId())));
        model.addAttribute("isSubmissionAllowed", isSubmissionAllowed);

        return "project/detail";
    }

    @RequestMapping(value = "/{projectId}/confirm-project-details", method = RequestMethod.GET)
    public String projectDetailConfirmSubmit(Model model, @PathVariable("projectId") final Long projectId,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Boolean isSubmissionAllowed = projectService.isSubmitAllowed(projectId).getSuccessObject();

        model.addAttribute("projectId", projectId);
        model.addAttribute("currentUser", loggedInUser);
        model.addAttribute("isSubmissionAllowed", isSubmissionAllowed);
        return "project/confirm-project-details";
    }

    @RequestMapping(value = "/{projectId}/details/finance-contact", method = RequestMethod.GET)
    public String viewFinanceContact(Model model,
                                     @PathVariable("projectId") final Long projectId,
                                     @RequestParam(value="organisation",required=false) Long organisation,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        FinanceContactForm form = new FinanceContactForm();
        InviteeForm inviteeForm = new InviteeForm();

        return doViewFinanceContact(model, projectId, organisation, loggedInUser, form, inviteeForm, true);
    }

    @RequestMapping(value = "/{projectId}/details/finance-contact", method = POST)
    public String updateFinanceContact(Model model,
                                       @PathVariable("projectId") final Long projectId,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) FinanceContactForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        InviteeForm inviteForm = new InviteeForm();

        Supplier<String> failureView = () -> doViewFinanceContact(model, projectId, form.getOrganisation(), loggedInUser, form, inviteForm, false);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = projectService.updateFinanceContact(projectId, form.getOrganisation(), form.getFinanceContact());

            return validationHandler.addAnyErrors(updateResult, toField("financeContact")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));
        });
    }

    @RequestMapping(value = "/{projectId}/details/invite-finance-contact", method = POST)
    public String inviteFinanceContact(Model model, @PathVariable("projectId") final Long projectId,
                                       @Valid @ModelAttribute(INVITE_FORM_ATTR_NAME) InviteeForm form,
                                       @RequestParam(value="organisation") Long organisation,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       @ModelAttribute("loggedInUser") UserResource loggedInUser
                                       ) {

        FinanceContactForm financeForm = new FinanceContactForm();

        Supplier<String> failureView = () -> doViewFinanceContact(model, projectId, organisation, loggedInUser, financeForm, form, false);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            InviteProjectResource invite = createProjectInviteResourceForNewContact (projectId, form.getName(), form.getEmail(), organisation);

            ServiceResult<Void> saveResult = projectService.saveProjectInvite(invite);

            InviteProjectResource savedInvite = projectService.getInvitesByProject(projectId).getSuccessObjectOrThrowException().stream()
                    .filter(i -> i.getEmail().equals(invite.getEmail())).findFirst().get();

            ServiceResult<Void> inviteResult = projectService.inviteFinanceContact(projectId, savedInvite);

            return validationHandler.addAnyErrors(saveResult, toField("financeContact")).
                    addAnyErrors(inviteResult, toField("financeContact")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));

        });

    }

    @RequestMapping(value = "/{projectId}/details/project-manager", method = RequestMethod.GET)
    public String viewProjectManager(Model model, @PathVariable("projectId") final Long projectId,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) throws InterruptedException, ExecutionException {

        ProjectManagerForm form = populateOriginalProjectManagerForm(projectId);
        return doViewProjectManager(model, projectId, loggedInUser, form);
    }

    @RequestMapping(value = "/{projectId}/details/project-manager", method = POST)
    public String updateProjectManager(Model model, @PathVariable("projectId") final Long projectId,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectManagerForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewProjectManager(model, projectId, loggedInUser, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectService.updateProjectManager(projectId, form.getProjectManager());

            return validationHandler.addAnyErrors(updateResult, toField("projectManager")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));
        });
    }

    @RequestMapping(value = "/{projectId}/details/start-date", method = RequestMethod.GET)
    public String viewStartDate(Model model, @PathVariable("projectId") final Long projectId,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsStartDateForm form,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);

        model.addAttribute("model", new ProjectDetailsStartDateViewModel(projectResource));
        LocalDate defaultStartDate = projectResource.getTargetStartDate().withDayOfMonth(1);
        form.setProjectStartDate(defaultStartDate);
        model.addAttribute(FORM_ATTR_NAME, form);

        return "project/details-start-date";
    }

    @RequestMapping(value = "/{projectId}/details/start-date", method = POST)
    public String updateStartDate(@PathVariable("projectId") final Long projectId,
                                  @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsStartDateForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                  Model model,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Supplier<String> failureView = () -> viewStartDate(model, projectId, form, loggedInUser);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectService.updateProjectStartDate(projectId, form.getProjectStartDate());

            return validationHandler.addAnyErrors(updateResult, toField("projectStartDate")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectDetails(projectId));
        });
    }

    @RequestMapping(value = "/{projectId}/details/project-address", method = RequestMethod.GET)
    public String viewAddress(Model model,
                              @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form,
                              @PathVariable("projectId") final Long projectId) {

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

    @RequestMapping(value = "/{projectId}/details/project-address", method = POST)
    public String updateAddress(Model model,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form,
                                @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                @PathVariable("projectId") final Long projectId) {

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
                if (validationHandler.hasErrors()) {
                    return viewCurrentAddressForm(model, form, projectResource);
                }
                newAddressResource = form.getAddressForm().getSelectedPostcode();
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
                    return viewAddress(model, form, projectId);
                },
                success -> redirectToProjectDetails(projectId));
    }

    @RequestMapping(value = "/{projectId}/details/project-address", params = SEARCH_ADDRESS, method = POST)
    public String searchAddress(Model model,
                                @PathVariable("projectId") Long projectId,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form,
                                BindingResult bindingResult) {

        form.getAddressForm().setSelectedPostcodeIndex(null);
        form.getAddressForm().setTriedToSearch(true);
        form.setAddressType(OrganisationAddressType.valueOf(form.getAddressType().name()));
        ProjectResource project = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, project);
    }

    @RequestMapping(value = "/{projectId}/details/project-address", params = SELECT_ADDRESS, method = POST)
    public String selectAddress(Model model,
                                @PathVariable("projectId") Long projectId,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form) {
        form.getAddressForm().setSelectedPostcode(null);
        ProjectResource project = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, project);
    }

    @RequestMapping(value = "/{projectId}/details/project-address", params = MANUAL_ADDRESS, method = POST)
    public String manualAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form,
                                @PathVariable("projectId") Long projectId) {
        AddressForm addressForm = form.getAddressForm();
        addressForm.setManualAddress(true);
        ProjectResource project = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, project);
    }

    @RequestMapping(value = "/{projectId}/details/submit", method = POST)
    public String submitProjectDetails(@PathVariable("projectId") Long projectId) {
        projectService.setApplicationDetailsSubmitted(projectId).getSuccessObjectOrThrowException();
        return redirectToProjectDetails(projectId);
    }

    @RequestMapping(value="/{projectId}/team-status", method = GET)
    public String viewProjectTeamStatus(Model model, @PathVariable("projectId") final Long projectId) {

        model.addAttribute("model", getProjectTeamStatusViewModel(projectId));
        return "project/consortium-status";
    }

    private ProjectConsortiumStatusViewModel getProjectTeamStatusViewModel(final Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        List<OrganisationResource> otherOrganisations = projectService.getPartnerOrganisationsForProject(projectId);

        Optional<MonitoringOfficerResource> monitoringOfficer = projectService.getMonitoringOfficerForProject(projectId);
        Optional<BankDetailsResource> leadBankDetails = bankDetailsService.getBankDetailsByProjectAndOrganisation(projectId, leadOrganisation.getId()).toOptionalIfNotFound().getSuccessObject();


        ConsortiumPartnerStatus leadProjectDetailsSubmitted = createProjectDetailsStatus(project);
        ConsortiumPartnerStatus monitoringOfficerStatus = createMonitoringOfficerStatus(monitoringOfficer, leadProjectDetailsSubmitted);
        ConsortiumPartnerStatus leadBankDetailsStatus = createBankDetailStatus(leadBankDetails);
        ConsortiumPartnerStatus financeChecksStatus = createFinanceCheckStatus(leadBankDetails, leadBankDetailsStatus);
        ConsortiumPartnerStatus spendProfileStatus = createSpendProfileStatus(financeChecksStatus);
        ConsortiumPartnerStatus otherDocumentsStatus = createOtherDocumentStatus(project);
        ConsortiumPartnerStatus grantOfferLetterStatus = createGrantOfferLetterStatus();

        final LeadPartnerModel leadPartnerModel = new LeadPartnerModel(
            leadOrganisation.getName(),
            leadProjectDetailsSubmitted,
            monitoringOfficerStatus,
            leadBankDetailsStatus,
            financeChecksStatus,
            spendProfileStatus,
            otherDocumentsStatus,
            grantOfferLetterStatus
        );

        final List<RegularPartnerModel> otherPartnersModels = otherOrganisations.stream().filter(partner -> !partner.getId().equals(leadOrganisation.getId())).map(partner -> createPartnerModel(project, partner)).collect(Collectors.toList());

        return new ProjectConsortiumStatusViewModel(projectId, leadPartnerModel, otherPartnersModels);
    }

    private RegularPartnerModel createPartnerModel(final ProjectResource project, final OrganisationResource partner) {

        Optional<BankDetailsResource> bankDetails = bankDetailsService.getBankDetailsByProjectAndOrganisation(project.getId(), partner.getId()).toOptionalIfNotFound().getSuccessObject();

        final String name = partner.getName();
        final ConsortiumPartnerStatus projectDetailsStatus = createProjectDetailsStatus(project);
        final ConsortiumPartnerStatus bankDetailsStatus = createBankDetailStatus(bankDetails);
        final ConsortiumPartnerStatus financeChecksStatus = createFinanceCheckStatus(bankDetails, bankDetailsStatus);
        final ConsortiumPartnerStatus spendProfileStatus = createSpendProfileStatus(financeChecksStatus);

        return new RegularPartnerModel(
            name,
            projectDetailsStatus,
            bankDetailsStatus,
            financeChecksStatus,
            spendProfileStatus
        );
    }

    private ConsortiumPartnerStatus createProjectDetailsStatus(final ProjectResource project) {
        return project.isProjectDetailsSubmitted()?COMPLETE:ACTION_REQUIRED;
    }

    private ConsortiumPartnerStatus createMonitoringOfficerStatus(final Optional<MonitoringOfficerResource> monitoringOfficer, final ConsortiumPartnerStatus leadProjectDetailsSubmitted) {
        if(leadProjectDetailsSubmitted.equals(COMPLETE)){
            return monitoringOfficer.isPresent()? COMPLETE : ConsortiumPartnerStatus.PENDING;
        }else{
            return NOT_STARTED;
        }

    }

    private ConsortiumPartnerStatus createBankDetailStatus(final Optional<BankDetailsResource> bankDetails) {
        if(bankDetails.isPresent()){
            return bankDetails.get().isApproved()?COMPLETE:ConsortiumPartnerStatus.PENDING;
        }else{
            return ACTION_REQUIRED;
        }
    }

    private ConsortiumPartnerStatus createFinanceCheckStatus(final Optional<BankDetailsResource> bankDetails, final ConsortiumPartnerStatus bankDetailsStatus) {
        if(bankDetailsStatus.equals(COMPLETE)){
            //TODO update logic when Finance checks are implemented
            return COMPLETE;
        }
        else{
            return NOT_STARTED;
        }
    }

    private ConsortiumPartnerStatus createSpendProfileStatus(final ConsortiumPartnerStatus financeChecksStatus) {
        if(financeChecksStatus.equals(COMPLETE)){
            //TODO update logic when spend profile is implemented
            return COMPLETE;
        }else{
            return NOT_STARTED;
        }
    }

    private ConsortiumPartnerStatus createOtherDocumentStatus(final ProjectResource project) {
        if(project.getCollaborationAgreement()!= null && project.getExploitationPlan()!= null){
            return COMPLETE;
        }else{
            return ACTION_REQUIRED;
        }
    }

    private ConsortiumPartnerStatus createGrantOfferLetterStatus() {
        //TODO update logic when GrantOfferLetter is implemented
        return NOT_STARTED;
    }

    private ProjectManagerForm populateOriginalProjectManagerForm(final Long projectId) {

        Optional<ProjectUserResource> existingProjectManager = getProjectManager(projectId);

        ProjectManagerForm form = new ProjectManagerForm();
        form.setProjectManager(existingProjectManager.map(ProjectUserResource::getId).orElse(null));
        return form;
    }

    private String doViewFinanceContact(Model model, Long projectId, Long organisation, UserResource loggedInUser, FinanceContactForm form, final InviteeForm inviteeForm, boolean setDefaultFinanceContact) {

        if(organisation == null) {
            return redirectToProjectDetails(projectId);
        }

        if(!userIsPartnerInOrganisationForProject(projectId, organisation, loggedInUser.getId())){
            return redirectToProjectDetails(projectId);
        }

        if(!anyUsersInGivenOrganisationForProject(projectId, organisation)){
            return redirectToProjectDetails(projectId);
        }

        return modelForFinanceContact(model, projectId, organisation, loggedInUser, form, inviteeForm, setDefaultFinanceContact);
    }

    private Optional<ProjectUserResource> getProjectManager(Long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getName().equals(pu.getRoleName()));
    }

    private void populateProjectManagerModel(Model model, final Long projectId, ProjectManagerForm form,
                                             ApplicationResource applicationResource) {

        ProjectResource projectResource = projectService.getById(projectId);
        List<ProjectUserResource> leadPartners = projectService.getLeadPartners(projectId);

        model.addAttribute("allUsers", leadPartners);
        model.addAttribute("project", projectResource);
        model.addAttribute("app", applicationResource);
        model.addAttribute(FORM_ATTR_NAME, form);
    }

    private boolean anyUsersInGivenOrganisationForProject(Long projectId, Long organisationId) {
        List<ProjectUserResource> thisProjectUsers = projectService.getProjectUsersForProject(projectId);
        List<ProjectUserResource> projectUsersForOrganisation = simpleFilter(thisProjectUsers, user -> user.getOrganisation().equals(organisationId));
        return !projectUsersForOrganisation.isEmpty();
    }

    private boolean userIsPartnerInOrganisationForProject(Long projectId, Long organisationId, Long userId) {
        if(userId == null) {
            return false;
        }

        List<ProjectUserResource> thisProjectUsers = projectService.getProjectUsersForProject(projectId);
        List<ProjectUserResource> projectUsersForOrganisation = simpleFilter(thisProjectUsers, user -> user.getOrganisation().equals(organisationId));
        List<ProjectUserResource> projectUsersForUserAndOrganisation = simpleFilter(projectUsersForOrganisation, user -> user.getUser().equals(userId));

        return !projectUsersForUserAndOrganisation.isEmpty();
    }

    private String modelForFinanceContact(Model model, Long projectId, Long organisation, UserResource loggedInUser, FinanceContactForm form, InviteeForm inviteeForm, boolean setDefaultFinanceContact) {

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        List<ProjectUserResource> financeContacts = simpleFilter(projectUsers, pr -> pr.isFinanceContact() && organisation.equals(pr.getOrganisation()));

        form.setOrganisation(organisation);

        if (setDefaultFinanceContact && !financeContacts.isEmpty()) {
            form.setFinanceContact(getOnlyElement(financeContacts).getUser());
        }

        return modelForFinanceContact(model, projectId, form, loggedInUser, inviteeForm);  //, organisation
    }

    private String modelForFinanceContact(Model model, Long projectId, FinanceContactForm form, UserResource loggedInUser, InviteeForm inviteeForm) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        List<FinanceContactModel> thisOrganisationUsers = userService.getOrganisationProcessRoles(applicationResource, form.getOrganisation()).stream()
                .map(user -> new FinanceContactModel(EXISTING, user.getUserName(), user.getUser()))
                .collect(toList());
        List<FinanceContactModel> invitedUsers = projectService.getInvitesByProject(projectId).getSuccessObjectOrThrowException().stream()
                .filter(invite -> form.getOrganisation().equals(invite.getOrganisation()))
                .map(invite -> new FinanceContactModel(PENDING, invite.getName() + " (Pending)", projectId))
                .collect(toList());

        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());

        SelectFinanceContactViewModel viewModel = new SelectFinanceContactViewModel(thisOrganisationUsers, invitedUsers, form.getOrganisation(), projectResource, loggedInUser.getId(), applicationResource, competitionResource);

        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute(INVITE_FORM_ATTR_NAME, inviteeForm);
        model.addAttribute("model", viewModel);
        return "project/finance-contact";
    }

    private String doViewProjectManager(Model model, Long projectId, UserResource loggedInUser, ProjectManagerForm form) {

        ProjectResource projectResource = projectService.getById(projectId);

        if(!projectService.isUserLeadPartner(projectResource.getId(), loggedInUser.getId())) {
            return redirectToProjectDetails(projectId);
        }

        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        populateProjectManagerModel(model, projectId, form, applicationResource);

        return "project/project-manager";
    }

    private String viewCurrentAddressForm(Model model, ProjectDetailsAddressForm form,
                                          ProjectResource project){
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = loadDataIntoModel(project);
        processAddressLookupFields(form);
        model.addAttribute("model", projectDetailsAddressViewModel);
        return "project/details-address";
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }

    private ProjectDetailsAddressViewModel loadDataIntoModel(final ProjectResource project){
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = new ProjectDetailsAddressViewModel(project);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(project.getId());

        Optional<OrganisationAddressResource> registeredAddress = getAddress(leadOrganisation, REGISTERED);
        if(registeredAddress.isPresent()){
            projectDetailsAddressViewModel.setRegisteredAddress(registeredAddress.get().getAddress());
        }

        Optional<OrganisationAddressResource> operatingAddress = getAddress(leadOrganisation, OPERATING);
        if(operatingAddress.isPresent()){
            projectDetailsAddressViewModel.setOperatingAddress(operatingAddress.get().getAddress());
        }

        Optional<OrganisationAddressResource> projectAddress = getAddress(leadOrganisation, PROJECT);
        if(projectAddress.isPresent()){
            projectDetailsAddressViewModel.setProjectAddress(projectAddress.get().getAddress());
        }

        return projectDetailsAddressViewModel;
    }

    private List<OrganisationResource> getPartnerOrganisations(final List<ProjectUserResource> projectRoles) {

        final Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);

        final Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        SortedSet<OrganisationResource> organisationSet = projectRoles.stream()
                .filter(uar -> uar.getRoleName().equals(PARTNER.getName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
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

        InviteProjectResource inviteResource = new InviteProjectResource();

        inviteResource.setProject(projectId);
        inviteResource.setName(name);
        inviteResource.setEmail (email);
        inviteResource.setOrganisation(organisationId);
        inviteResource.setInviteOrganisation(organisationId);
        inviteResource.setApplicationId(projectResource.getApplication());
        inviteResource.setLeadOrganisation(leadOrganisation.getName());

        return inviteResource;
    }
}
