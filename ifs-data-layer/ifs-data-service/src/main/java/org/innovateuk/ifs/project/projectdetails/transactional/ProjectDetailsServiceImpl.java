package org.innovateuk.ifs.project.projectdetails.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.PostcodeAndTownResource;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ExceptionThrowingFunction;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.invite.mapper.ProjectUserInviteMapper;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.monitoringofficer.repository.LegacyMonitoringOfficerRepository;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.commons.validation.ValidationConstants.MAX_POSTCODE_LENGTH;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.project.resource.ProjectState.WITHDRAWN;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;

/**
 * Transactional and secure service for Project Details processing work
 */
@Service
public class ProjectDetailsServiceImpl extends AbstractProjectServiceImpl implements ProjectDetailsService {

    private static final String WEB_CONTEXT = "/project-setup";
    private static final int MAX_TOWN_LENGTH = 255;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    private ProjectUserInviteMapper projectInviteMapper;

    @Autowired
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private LegacyMonitoringOfficerRepository legacyMonitoringOfficerRepository;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_FINANCE_CONTACT,
        INVITE_PROJECT_MANAGER
    }

    @Override
    public ServiceResult<ProjectUserResource> getProjectManager(Long projectId) {
        return find(projectUserRepository.findByProjectIdAndRole(projectId, PROJECT_MANAGER),
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

    private ServiceResult<Project> validateGOLGenerated(Project project, CommonFailureKeys failKey) {
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
        Organisation leadPartnerOrganisation = organisationRepository.findById(leadRole.getOrganisationId()).orElse(null);
        return simpleFilter(project.getProjectUsers(), pu -> organisationsEqual(leadPartnerOrganisation, pu)
                && pu.getRole().isPartner());
    }

    private boolean organisationsEqual(Organisation leadPartnerOrganisation, ProjectUser pu) {
        return pu.getOrganisation().getId().equals(leadPartnerOrganisation.getId());
    }

    private ServiceResult<Void> createOrUpdateProjectManagerForProject(Project project, ProjectUser leadPartnerUser) {

        Optional<ProjectUser> existingProjectManager = getExistingProjectManager(project);

        return existingProjectManager.map(pm -> {
            pm.setUser(leadPartnerUser.getUser());
            pm.setOrganisation(leadPartnerUser.getOrganisation());
            return serviceSuccess();

        }).orElseGet(() -> {
            ProjectUser projectUser = new ProjectUser(leadPartnerUser.getUser(), leadPartnerUser.getProcess(),
                    PROJECT_MANAGER, leadPartnerUser.getOrganisation());
            project.addProjectUser(projectUser);
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
        return getProject(projectId).andOnSuccess(project ->
            validateProjectDuration(durationInMonths).
            andOnSuccess(() -> validateIfProjectDurationCanBeChanged(project)).
            andOnSuccessReturnVoid(() -> project.setDurationInMonths(durationInMonths)));
    }

    private ServiceResult<Void> validateProjectDuration(long durationInMonths) {

        if (durationInMonths <1) {
            return serviceFailure(PROJECT_SETUP_PROJECT_DURATION_MUST_BE_MINIMUM_ONE_MONTH);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> validateIfProjectDurationCanBeChanged(Project project) {

        if (isSpendProfileIsGenerated(project.getId())) {
            return serviceFailure(PROJECT_SETUP_PROJECT_DURATION_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED);
        }

        if (isProjectWithdrawn(project)) {
            return serviceFailure(GENERAL_FORBIDDEN);
        }

        return serviceSuccess();
    }

    private boolean isProjectWithdrawn(Project project) {
        ProjectState projectState = projectWorkflowHandler.getState(project);
        return WITHDRAWN.equals(projectState);
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

        List<ProjectUser> existingFinanceContactForOrganisation = project.getProjectUsers(pu -> pu.getOrganisation().equals(newFinanceContact.getOrganisation()) && PROJECT_FINANCE_CONTACT.equals(pu.getRole()));
        existingFinanceContactForOrganisation.forEach(project::removeProjectUser);
        project.addProjectUser(newFinanceContact);
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> updatePartnerProjectLocation(ProjectOrganisationCompositeId composite, PostcodeAndTownResource postcodeAndTown) {
        Function<PostcodeAndTownResource, ServiceResult<Void>> validation;
        ExceptionThrowingFunction<PartnerOrganisation, PartnerOrganisation> settingLocation;

        Optional<Organisation> organisation = organisationRepository.findById(composite.getOrganisationId());

        if (organisation.isPresent() && organisation.get().isInternational()) {
            validation = ProjectDetailsServiceImpl::validateTown;
            settingLocation = settingTown(postcodeAndTown);
        } else {
            validation = ProjectDetailsServiceImpl::validatePostcode;
            settingLocation = settingPostcode(postcodeAndTown);
        }

        return validation.apply(postcodeAndTown).
            andOnSuccess(() -> getProject(composite.getProjectId())).
            andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_LOCATION_CANNOT_BE_UPDATED_IF_GOL_GENERATED)).
            andOnSuccess(() -> getPartnerOrganisation(composite.getProjectId(), composite.getOrganisationId())).
            andOnSuccessReturn(settingLocation)
            .andOnSuccessReturnVoid(partnerOrganisation ->
                getCurrentlyLoggedInProjectUser(partnerOrganisation.getProject(), PROJECT_PARTNER)
                        .andOnSuccessReturnVoid((projectUser) ->
                                projectDetailsWorkflowHandler.projectLocationAdded(partnerOrganisation.getProject(), projectUser)));
    }

    private static ExceptionThrowingFunction<PartnerOrganisation, PartnerOrganisation> settingPostcode(PostcodeAndTownResource postcodeAndTown) {
        return partnerOrganisation -> {
            partnerOrganisation.setPostcode(postcodeAndTown.getPostcode().toUpperCase());
            return partnerOrganisation;
        };
    }

    private static ExceptionThrowingFunction<PartnerOrganisation, PartnerOrganisation> settingTown(PostcodeAndTownResource postcodeAndTown) {
        return partnerOrganisation -> {
            String town = postcodeAndTown.getTown();
            String townFixedCase = Stream.of(town.split(" "))
                    .filter(w -> w.length() > 0)
                    .map(word -> {
                        String wordCased = word.substring(0, 1).toUpperCase();
                        if (word.length() > 1) {
                            wordCased = wordCased + word.substring(1).toLowerCase();
                        }
                        return wordCased;
                    })
                    .collect(Collectors.joining(" "));
            partnerOrganisation.setInternationalLocation(townFixedCase);
            return partnerOrganisation;
        };
    }

    private static ServiceResult<Void> validatePostcode(PostcodeAndTownResource postcodeAndTown) {
        String postcode = postcodeAndTown.getPostcode();

        if (StringUtils.isBlank(postcode)) {
            return serviceFailure(new Error("validation.field.must.not.be.blank", HttpStatus.BAD_REQUEST));
        }
        if (StringUtils.length(postcode) > MAX_POSTCODE_LENGTH) {
            return serviceFailure(new Error("validation.field.too.many.characters", asList("", MAX_POSTCODE_LENGTH), HttpStatus.BAD_REQUEST));
        }

        return serviceSuccess();
    }

    private static ServiceResult<Void> validateTown(PostcodeAndTownResource postcodeAndTown) {
        String town = postcodeAndTown.getTown();
        if (StringUtils.isBlank(town)) {
            return serviceFailure(new Error("validation.field.must.not.be.blank", HttpStatus.BAD_REQUEST));
        }

        if (StringUtils.length(town) > MAX_TOWN_LENGTH) {
            return serviceFailure(new Error("validation.field.too.many.characters", asList("", MAX_TOWN_LENGTH), HttpStatus.BAD_REQUEST));
        }

        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateProjectAddress(Long projectId, AddressResource address) {
        return getProject(projectId).
                andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_PROJECT_ADDRESS_CANNOT_BE_UPDATED_IF_GOL_GENERATED)).
                andOnSuccess(() ->
                        find(getProject(projectId)).
                                andOnSuccess(project -> {
                                    if (project.getAddress() != null) {
                                        project.getAddress().copyFrom(address);
                                    } else {
                                        project.setAddress(addressRepository.save(addressMapper.mapToDomain(address)));
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
    public ServiceResult<Void> inviteFinanceContact(Long projectId, ProjectUserInviteResource inviteResource) {
        return getProject(projectId)
                .andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_FINANCE_CONTACT_CANNOT_BE_UPDATED_IF_GOL_GENERATED))
                .andOnSuccess(() -> inviteContact(projectId, inviteResource, Notifications.INVITE_FINANCE_CONTACT));
    }

    @Override
    @Transactional
    public ServiceResult<Void> inviteProjectManager(Long projectId, ProjectUserInviteResource inviteResource) {

        return getProject(projectId)
                .andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED))
                .andOnSuccess(() -> inviteContact(projectId, inviteResource, Notifications.INVITE_PROJECT_MANAGER));
    }

    private ServiceResult<Void> inviteContact(Long projectId, ProjectUserInviteResource projectResource, Notifications kindOfNotification) {

        ProjectUserInvite projectInvite = projectInviteMapper.mapToDomain(projectResource);
        projectInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now());
        projectUserInviteRepository.save(projectInvite);

        Notification notification = new Notification(systemNotificationSource, createInviteContactNotificationTarget(projectInvite), kindOfNotification, createGlobalArgsForInviteContactEmail(projectId, projectResource));

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private NotificationTarget createInviteContactNotificationTarget(ProjectInvite projectInvite) {
        return new UserNotificationTarget(projectInvite.getName(), projectInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForInviteContactEmail(Long projectId, ProjectUserInviteResource inviteResource) {
        Project project = projectRepository.findById(projectId).get();
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findById(leadRole.getOrganisationId()).get();
        String leadOrganisationName = leadOrganisation.getName();
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", project.getName());
        globalArguments.put("applicationId", inviteResource.getApplicationId());
        globalArguments.put("leadOrganisation", leadOrganisationName);
        globalArguments.put("inviteOrganisationName", inviteResource.getOrganisationName());
        globalArguments.put("competitionName", inviteResource.getCompetitionName());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, inviteResource));
        return globalArguments;
    }

    private String getInviteUrl(String baseUrl, ProjectUserInviteResource inviteResource) {
        return String.format("%s/accept-invite/%s", baseUrl, inviteResource.getHash());
    }


    private ServiceResult<ProjectUser> getCurrentlyLoggedInPartner(Project project) {
        return getCurrentlyLoggedInProjectUser(project, PROJECT_PARTNER);
    }

    private ServiceResult<ProjectUser> getCurrentlyLoggedInProjectUser(Project project, ProjectParticipantRole role) {

        return getCurrentlyLoggedInUser().andOnSuccess(currentUser ->
                simpleFindFirst(project.getProjectUsers(), pu -> findUserAndRole(role, currentUser, pu)).
                        map(ServiceResult::serviceSuccess).
                        orElse(serviceFailure(forbiddenError())));
    }

    private boolean findUserAndRole(ProjectParticipantRole role, User currentUser, ProjectUser pu) {
        return pu.getUser().getId().equals(currentUser.getId()) && pu.getRole().equals(role);
    }
}
