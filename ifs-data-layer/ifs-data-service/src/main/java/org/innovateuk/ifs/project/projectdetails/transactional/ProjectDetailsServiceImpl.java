package org.innovateuk.ifs.project.projectdetails.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.invite.mapper.InviteProjectMapper;
import org.innovateuk.ifs.invite.repository.InviteProjectRepository;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.innovateuk.ifs.project.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.transactional.EmailService;
import org.innovateuk.ifs.project.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElementOrEmpty;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;

/**
 * Transactional and secure service for Project Details processing work
 */
@Service
public class ProjectDetailsServiceImpl extends AbstractProjectServiceImpl implements ProjectDetailsService {
    private static final Log LOG = LogFactory.getLog(org.innovateuk.ifs.project.transactional.ProjectServiceImpl.class);

    public static final String WEB_CONTEXT = "/project-setup";

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationAddressRepository organisationAddressRepository;

    @Autowired
    private AddressTypeRepository addressTypeRepository;

    @Autowired
    private EmailService projectEmailService;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private GrantOfferLetterWorkflowHandler grantOfferLetterWorkflowHandler;

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    private InviteProjectRepository inviteProjectRepository;

    @Autowired
    private InviteProjectMapper inviteProjectMapper;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private StatusService statusService;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_FINANCE_CONTACT,
        INVITE_PROJECT_MANAGER
    }

    @Override
    @Transactional
    public ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerUserId) {
        return getProject(projectId).
                andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED)).
                andOnSuccess(project -> validateProjectManager(project, projectManagerUserId).
                        andOnSuccess(leadPartner -> createOrUpdateProjectManagerForProject(project, leadPartner)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return validateProjectStartDate(projectStartDate).
                andOnSuccess(() -> getProject(projectId)).
                andOnSuccess(this::validateIfProjectAlreadySubmitted).
                andOnSuccessReturnVoid(project -> project.setTargetStartDate(projectStartDate));
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId) {
        return getProject(composite.getProjectId()).
                andOnSuccess(this::validateProjectIsInSetup).
                andOnSuccess(project -> validateProjectOrganisationFinanceContact(project, composite.getOrganisationId(), financeContactUserId).
                        andOnSuccess(projectUser -> createFinanceContactProjectUser(projectUser.getUser(), project, projectUser.getOrganisation()).
                                andOnSuccessReturnVoid(financeContact -> addFinanceContactToProject(project, financeContact))));
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateProjectAddress(Long organisationId, Long projectId, OrganisationAddressType organisationAddressType, AddressResource address) {
        return getProject(projectId).
                andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_PROJECT_ADDRESS_CANNOT_BE_UPDATED_IF_GOL_GENERATED)).
                andOnSuccess(() ->
                        find(getProject(projectId), getOrganisation(organisationId)).
                                andOnSuccess((project, organisation) -> {
                                    if (address.getId() != null && addressRepository.exists(address.getId())) {
                                        Address existingAddress = addressRepository.findOne(address.getId());
                                        project.setAddress(existingAddress);
                                    } else {
                                        Address newAddress = addressMapper.mapToDomain(address);
                                        if (address.getOrganisations() == null || address.getOrganisations().size() == 0) {
                                            AddressType addressType = addressTypeRepository.findOne(organisationAddressType.getOrdinal());
                                            List<OrganisationAddress> existingOrgAddresses = organisationAddressRepository.findByOrganisationIdAndAddressType(organisation.getId(), addressType);
                                            existingOrgAddresses.forEach(oA -> organisationAddressRepository.delete(oA));
                                            OrganisationAddress organisationAddress = new OrganisationAddress(organisation, newAddress, addressType);
                                            organisationAddressRepository.save(organisationAddress);
                                        }
                                        project.setAddress(newAddress);
                                    }

                                    return getCurrentlyLoggedInPartner(project).andOnSuccess(user -> {
                                        projectDetailsWorkflowHandler.projectAddressAdded(project, user);
                                        return serviceSuccess();
                                    });
                                })
                );
    }

    @Override
    @Transactional
    public ServiceResult<Void> submitProjectDetails(final Long projectId, ZonedDateTime date) {

        return getProject(projectId).andOnSuccess(project ->
                getCurrentlyLoggedInPartner(project).andOnSuccess(projectUser -> {

                    if (projectDetailsWorkflowHandler.submitProjectDetails(project, projectUser)) {
                        return serviceSuccess();
                    } else {
                        return serviceFailure(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_SUBMITTED_IF_INCOMPLETE);
                    }
                }));
    }

    @Override
    public ServiceResult<Boolean> isSubmitAllowed(Long projectId) {
        return getProject(projectId).andOnSuccessReturn(this::doIsSubmissionAllowed);
    }

    @Override
    @Transactional
    public ServiceResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource) {
        return inviteContact(projectId, inviteResource, Notifications.INVITE_FINANCE_CONTACT);
    }

    @Override
    @Transactional
    public ServiceResult<Void> inviteProjectManager(Long projectId, InviteProjectResource inviteResource) {

        return getProject(projectId)
                .andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED))
                .andOnSuccess(() -> inviteContact(projectId, inviteResource, Notifications.INVITE_PROJECT_MANAGER));
    }

    private ServiceResult<Project> validateIfProjectAlreadySubmitted(final Project project) {

        if (projectDetailsWorkflowHandler.isSubmitted(project)) {
            return serviceFailure(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED);
        }

        return serviceSuccess(project);
    }

    private ServiceResult<Project> validateGOLGenerated(Project project, CommonFailureKeys failKey){
        return statusService.getProjectStatusByProjectId(project.getId()).andOnSuccess(
                projectStatus -> {
                    if(projectStatus.getSpendProfileStatus().equals(COMPLETE)){
                        return serviceFailure(failKey);
                    }
                    return serviceSuccess(project);
                }
        );
    }

    private ServiceResult<ProjectUser> validateProjectManager(Project project, Long projectManagerUserId) {

        List<ProjectUser> leadPartners = getLeadPartners(project);
        List<ProjectUser> matchingProjectUsers = simpleFilter(leadPartners, pu -> pu.getUser().getId().equals(projectManagerUserId));

        if (!matchingProjectUsers.isEmpty()) {
            return getOnlyElementOrFail(matchingProjectUsers);
        } else {
            return serviceFailure(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER);
        }
    }

    private List<ProjectUser> getLeadPartners(Project project) {
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadPartnerOrganisation = organisationRepository.findOne(leadRole.getOrganisationId());
        return simpleFilter(project.getProjectUsers(), pu -> organisationsEqual(leadPartnerOrganisation, pu)
                && pu.getRole().isPartner());
    }

    private boolean organisationsEqual(Organisation leadPartnerOrganisation, ProjectUser pu) {
        return pu.getOrganisation().getId().equals(leadPartnerOrganisation.getId());
    }

    private ServiceResult<Void> createOrUpdateProjectManagerForProject(Project project, ProjectUser leadPartnerUser) {

        Optional<ProjectUser> existingProjectManager = getExistingProjectManager(project);

        ServiceResult<Void> setProjectManagerResult = existingProjectManager.map(pm -> {
            pm.setUser(leadPartnerUser.getUser());
            pm.setOrganisation(leadPartnerUser.getOrganisation());
            return serviceSuccess();

        }).orElseGet(() -> {
            ProjectUser projectUser = new ProjectUser(leadPartnerUser.getUser(), leadPartnerUser.getProcess(),
                    PROJECT_MANAGER, leadPartnerUser.getOrganisation());
            project.addProjectUser(projectUser);
            return serviceSuccess();
        });

        return setProjectManagerResult.andOnSuccess(result -> {
            projectDetailsWorkflowHandler.projectManagerAdded(project, leadPartnerUser);
            return serviceSuccess();
        });
    }

    private Optional<ProjectUser> getExistingProjectManager(Project project) {
        List<ProjectUser> projectUsers = project.getProjectUsers();
        List<ProjectUser> projectManagers = simpleFilter(projectUsers, pu -> pu.getRole().isProjectManager());
        return getOnlyElementOrEmpty(projectManagers);
    }

    private ServiceResult<Void> validateProjectStartDate(LocalDate date) {

        if (date.getDayOfMonth() != 1) {
            return serviceFailure(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH);
        }

        if (date.isBefore(LocalDate.now())) {
            return serviceFailure(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE);
        }

        return serviceSuccess();
    }

    private ServiceResult<Project> validateProjectIsInSetup(final Project project) {
        if(!ProjectState.SETUP.equals(projectWorkflowHandler.getState(project))) {
            return serviceFailure(PROJECT_SETUP_ALREADY_COMPLETE);
        }

        return serviceSuccess(project);
    }

    private ServiceResult<ProjectUser> validateProjectOrganisationFinanceContact(Project project, Long organisationId, Long financeContactUserId) {

        ServiceResult<ProjectUser> result = find(organisation(organisationId))
                .andOnSuccessReturn(organisation -> project.getExistingProjectUserWithRoleForOrganisation(PROJECT_FINANCE_CONTACT, organisation));

        if (result.isFailure()) {
            return result;
        }

        List<ProjectUser> projectUsers = project.getProjectUsers();

        List<ProjectUser> matchingUserOrganisationProcessRoles = simpleFilter(projectUsers,
                pr -> organisationId.equals(pr.getOrganisation().getId()) && financeContactUserId.equals(pr.getUser().getId()));

        if (matchingUserOrganisationProcessRoles.isEmpty()) {
            return serviceFailure(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION);
        }

        List<ProjectUser> partnerUsers = simpleFilter(matchingUserOrganisationProcessRoles, ProjectUser::isPartner);

        if (partnerUsers.isEmpty()) {
            return serviceFailure(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION);
        }

        return getOnlyElementOrFail(partnerUsers);
    }

    private ServiceResult<ProjectUser> createFinanceContactProjectUser(User user, Project project, Organisation organisation) {
        return createProjectUserForRole(project, user, organisation, PROJECT_FINANCE_CONTACT);
    }

    private ServiceResult<ProjectUser> createProjectUserForRole(Project project, User user, Organisation organisation, ProjectParticipantRole role) {
        return serviceSuccess(new ProjectUser(user, project, role, organisation));
    }

    private ServiceResult<Void> addFinanceContactToProject(Project project, ProjectUser newFinanceContact) {

        List<ProjectUser> existingFinanceContactForOrganisation = project.getProjectUsers(pu -> pu.getOrganisation().equals(newFinanceContact.getOrganisation()) && ProjectParticipantRole.PROJECT_FINANCE_CONTACT.equals(pu.getRole()));
        existingFinanceContactForOrganisation.forEach(project::removeProjectUser);
        project.addProjectUser(newFinanceContact);
        return serviceSuccess();
    }

    private boolean doIsSubmissionAllowed(Project project) {
        return projectDetailsWorkflowHandler.isSubmissionAllowed(project);
    }

    private ServiceResult<Void> inviteContact(Long projectId, InviteProjectResource projectResource, Notifications kindOfNotification) {

        ProjectInvite projectInvite = inviteProjectMapper.mapToDomain(projectResource);
        ServiceResult<Void> inviteContactEmailSendResult = projectEmailService.sendEmail(singletonList(createInviteContactNotificationTarget(projectInvite)), createGlobalArgsForInviteContactEmail(projectId, projectResource), kindOfNotification);
        inviteContactEmailSendResult.handleSuccessOrFailure(
                failure -> handleInviteError(projectInvite, failure),
                success -> handleInviteSuccess(projectInvite)
        );
        return inviteContactEmailSendResult;
    }

    private NotificationTarget createInviteContactNotificationTarget(ProjectInvite projectInvite) {
        return new ExternalUserNotificationTarget(projectInvite.getName(), projectInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForInviteContactEmail(Long projectId, InviteProjectResource inviteResource) {
        Project project = projectRepository.findOne(projectId);
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadRole.getOrganisationId());
        String leadOrganisationName = leadOrganisation.getName();
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", project.getName());
        globalArguments.put("leadOrganisation", leadOrganisationName);
        globalArguments.put("inviteOrganisationName", inviteResource.getOrganisationName());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, inviteResource));
        return globalArguments;
    }

    private String getInviteUrl(String baseUrl, InviteProjectResource inviteResource) {
        return String.format("%s/accept-invite/%s", baseUrl, inviteResource.getHash());
    }

    private ServiceResult<Boolean> handleInviteError(ProjectInvite i, ServiceFailure failure) {
        LOG.error(String.format("Invite failed %s , %s (error count: %s)", i.getId(), i.getEmail(), failure.getErrors().size()));
        List<Error> errors = failure.getErrors();
        return serviceFailure(errors);
    }

    private boolean handleInviteSuccess(ProjectInvite projectInvite) {
        inviteProjectRepository.save(projectInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
        return true;
    }
}
