package org.innovateuk.ifs.project.projectdetails.transactional;

import org.apache.commons.lang3.StringUtils;
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
import org.innovateuk.ifs.invite.repository.ProjectInviteRepository;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.commons.validation.ValidationConstants.MAX_POST_CODE_LENGTH;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
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
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    private ProjectInviteRepository projectInviteRepository;

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
    public ServiceResult<ProjectUserResource> getProjectManager(Long projectId) {
        return find(projectUserRepository.findByProjectIdAndRole(projectId, ProjectParticipantRole.PROJECT_MANAGER),
                notFoundError(ProjectUserResource.class, projectId)).andOnSuccessReturn(projectUserMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerUserId) {
        return getProject(projectId).
                andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED)).
                andOnSuccess(project -> validateProjectManager(project, projectManagerUserId).
                        andOnSuccess(leadPartner -> createOrUpdateProjectManagerForProject(project, leadPartner)));
    }

    private ServiceResult<Project> validateGOLGenerated(Project project, CommonFailureKeys failKey){
        if (project.getGrantOfferLetter() != null){
            return serviceFailure(failKey);
        }
        return serviceSuccess(project);
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

    @Override
    @Transactional
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return validateProjectStartDate(projectStartDate).
                andOnSuccess(() -> validateIfStartDateCanBeChanged(projectId)).
                andOnSuccess(() -> getProject(projectId)).
                andOnSuccessReturnVoid(project -> project.setTargetStartDate(projectStartDate));
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

    private ServiceResult<Void> validateIfStartDateCanBeChanged(Long projectId) {

        if (isSpendProfileIsGenerated(projectId)) {
            return serviceFailure(PROJECT_SETUP_START_DATE_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED);
        }

        return serviceSuccess();
    }

    private boolean isSpendProfileIsGenerated(Long projectId) {
        List<SpendProfile> spendProfiles = getSpendProfileByProjectId(projectId);
        return !spendProfiles.isEmpty();
    }

    private List<SpendProfile> getSpendProfileByProjectId(Long projectId) {
        return spendProfileRepository.findByProjectId(projectId);
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateProjectDuration(long projectId, long durationInMonths) {
        return validateProjectDuration(durationInMonths).
                andOnSuccess(() -> validateIfProjectDurationCanBeChanged(projectId)).
                andOnSuccess(() -> getProject(projectId)).
                andOnSuccessReturnVoid(project -> project.setDurationInMonths(durationInMonths));
    }

    private ServiceResult<Void> validateProjectDuration(long durationInMonths) {

        if (durationInMonths <1) {
            return serviceFailure(PROJECT_SETUP_PROJECT_DURATION_MUST_BE_MINIMUM_ONE_MONTH);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> validateIfProjectDurationCanBeChanged(long projectId) {

        if (isSpendProfileIsGenerated(projectId)) {
            return serviceFailure(PROJECT_SETUP_PROJECT_DURATION_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED);
        }

        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId) {
        return getProject(composite.getProjectId()).
                andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_FINANCE_CONTACT_CANNOT_BE_UPDATED_IF_GOL_GENERATED)).
                andOnSuccess(project -> validateProjectOrganisationFinanceContact(project, composite.getOrganisationId(), financeContactUserId).
                        andOnSuccess(projectUser -> createFinanceContactProjectUser(projectUser.getUser(), project, projectUser.getOrganisation()).
                                andOnSuccessReturnVoid(financeContact -> addFinanceContactToProject(project, financeContact))));
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

    @Override
    @Transactional
    public ServiceResult<Void> updatePartnerProjectLocation(ProjectOrganisationCompositeId composite, String postCode) {
        return validatePostCode(postCode).
                andOnSuccess(() -> validateIfPartnerProjectLocationCanBeChanged(composite.getProjectId())).
                andOnSuccess(() -> getPartnerOrganisation(composite.getProjectId(), composite.getOrganisationId())).
                andOnSuccessReturnVoid(partnerOrganisation -> partnerOrganisation.setPostCode(postCode.toUpperCase()));
    }

    private ServiceResult<Void> validatePostCode(String postCode) {
        if (StringUtils.isBlank(postCode)) {
            return serviceFailure(new Error("validation.field.must.not.be.blank", HttpStatus.BAD_REQUEST));
        }

        if (StringUtils.length(postCode) > MAX_POST_CODE_LENGTH) {
            return serviceFailure(new Error("validation.field.too.many.characters", asList("", MAX_POST_CODE_LENGTH), HttpStatus.BAD_REQUEST));
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> validateIfPartnerProjectLocationCanBeChanged(long projectId) {
        if (isMonitoringOfficerAssigned(projectId)) {
            return serviceFailure(PROJECT_SETUP_PARTNER_PROJECT_LOCATION_CANNOT_BE_CHANGED_ONCE_MONITORING_OFFICER_HAS_BEEN_ASSIGNED);
        }
        return serviceSuccess();
    }

    private boolean isMonitoringOfficerAssigned(long projectId) {
        MonitoringOfficer monitoringOfficer = getMonitoringOfficerByProjectId(projectId);
        return monitoringOfficer != null;
    }

    private MonitoringOfficer getMonitoringOfficerByProjectId(long projectId) {
        return monitoringOfficerRepository.findOneByProjectId(projectId);
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
    public ServiceResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource) {
        return getProject(projectId)
                .andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_FINANCE_CONTACT_CANNOT_BE_UPDATED_IF_GOL_GENERATED))
                .andOnSuccess(() -> inviteContact(projectId, inviteResource, Notifications.INVITE_FINANCE_CONTACT));
    }

    @Override
    @Transactional
    public ServiceResult<Void> inviteProjectManager(Long projectId, InviteProjectResource inviteResource) {

        return getProject(projectId)
                .andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED))
                .andOnSuccess(() -> inviteContact(projectId, inviteResource, Notifications.INVITE_PROJECT_MANAGER));
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
        return new UserNotificationTarget(projectInvite.getName(), projectInvite.getEmail());
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
        projectInviteRepository.save(projectInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
        return true;
    }
}
