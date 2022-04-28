package org.innovateuk.ifs.invite.transactional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.mapper.RoleInviteMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.invite.repository.RoleInviteRepository;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.SimpleOrganisation;
import org.innovateuk.ifs.organisation.repository.SimpleOrganisationRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service implementation providing operations around invites for users.
 */
@Slf4j
@Service
public class InviteUserServiceImpl extends BaseTransactionalService implements InviteUserService {

    @Autowired
    private RoleInviteRepository roleInviteRepository;

    @Autowired
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private RoleInviteMapper roleInviteMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private SimpleOrganisationRepository simpleOrganisationRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    public static final String INTERNAL_USER_WEB_CONTEXT = "/management/registration";
    public static final String EXTERNAL_USER_WEB_CONTEXT = "/registration";

    enum Notifications {
        INVITE_INTERNAL_USER,
        INVITE_EXTERNAL_USER,
    }

    @Value("${ifs.system.internal.user.email.domains:iuk.ukri.org}")
    private String internalUserEmailDomains;

    @Value("${ifs.system.kta.user.email.domain}")
    private String ktaUserEmailDomain;

    @Override
    @Transactional
    public ServiceResult<Void> saveUserInvite(UserResource invitedUser, Role role, String organisation) {
        if (StringUtils.isEmpty(invitedUser.getEmail()) || StringUtils.isEmpty(invitedUser.getFirstName())
                || StringUtils.isEmpty(invitedUser.getLastName()) || role == null){
            return serviceFailure(USER_ROLE_INVITE_INVALID);
        }

        if (externalRolesToInvite().contains(role)) {
            return validateExternalUserEmailDomain(invitedUser.getEmail(), role)
                    .andOnSuccess(() -> validateAndSaveInvite(invitedUser, role, organisation))
                    .andOnSuccess(this::inviteExternalUser);
        } else if (internalRoles().contains(role)) {
            return validateInternalUserEmailDomain(invitedUser.getEmail())
                    .andOnSuccess(() -> validateAndSaveInvite(invitedUser, role, organisation))
                    .andOnSuccess(this::inviteInternalUser);
        } else {
            return serviceFailure(NOT_AN_INTERNAL_USER_ROLE);
        }
    }

    private ServiceResult<RoleInvite> validateAndSaveInvite(UserResource invitedUser, Role role, String organisation) {
                return validateUserEmailAvailable(invitedUser)
                .andOnSuccess(() -> validateUserNotAlreadyInvited(invitedUser))
                .andOnSuccess(() -> saveInvite(invitedUser, role, organisation));

    }

    private ServiceResult<Void> validateInternalUserEmailDomain(String email) {

        String domain = StringUtils.substringAfter(email, "@");

        String[] domains = internalUserEmailDomains.split(",");

        boolean isInternal = Stream.of(domains).anyMatch(acceptedDomain -> acceptedDomain.equals(domain));

        if (!isInternal) {
            return serviceFailure(USER_ROLE_INVITE_INVALID_EMAIL);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> validateExternalUserEmailDomain(String email, Role role) {

        if (role == Role.KNOWLEDGE_TRANSFER_ADVISER) {
            ktaUserEmailDomain = StringUtils.defaultString(ktaUserEmailDomain);

            String domain = StringUtils.substringAfter(email, "@");

            if (!ktaUserEmailDomain.equalsIgnoreCase(domain)) {
                return serviceFailure(KTA_USER_ROLE_INVITE_INVALID_EMAIL);
            }
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> validateUserEmailAvailable(UserResource invitedUser) {
        return userRepository.findByEmail(invitedUser.getEmail()).isPresent() ? serviceFailure(USER_ROLE_INVITE_EMAIL_TAKEN) : serviceSuccess() ;
    }

    private ServiceResult<Void> validateUserNotAlreadyInvited(UserResource invitedUser) {

        List<RoleInvite> existingInvites = roleInviteRepository.findByEmail(invitedUser.getEmail());
        return existingInvites.isEmpty() ? serviceSuccess() : serviceFailure(USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED);
    }

    private ServiceResult<RoleInvite> saveInvite(UserResource invitedUser, Role role, String organisation) {
        SimpleOrganisation simpleOrganisation = null;
        if (organisation != null) {
            simpleOrganisation = simpleOrganisationRepository.save(new SimpleOrganisation(organisation));
        }
        RoleInvite roleInvite = new RoleInvite(invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                role,
                CREATED,
                simpleOrganisation);

        RoleInvite invite = roleInviteRepository.save(roleInvite);

        return serviceSuccess(invite);
    }
    private ServiceResult<Void> inviteInternalUser(RoleInvite roleInvite) {

        try {
            Map<String, Object> globalArgs = createGlobalArgsForInternalUserInvite(roleInvite);

            Notification notification = new Notification(systemNotificationSource,
                    createUserNotificationTarget(roleInvite),
                    Notifications.INVITE_INTERNAL_USER, globalArgs);

            ServiceResult<Void> inviteContactEmailSendResult = notificationService.sendNotificationWithFlush(notification, EMAIL);

            inviteContactEmailSendResult.handleSuccessOrFailure(
                    failure -> handleInviteError(roleInvite, failure),
                    success -> handleInviteSuccess(roleInvite)
            );
            return inviteContactEmailSendResult;
        } catch (IllegalArgumentException e) {
            log.error(String.format("Role %s lookup failed for user %s", roleInvite.getEmail(), roleInvite.getTarget().name()), e);
            return ServiceResult.serviceFailure(new Error(CommonFailureKeys.ADMIN_INVALID_USER_ROLE));
        }
    }
    private ServiceResult<Void> inviteExternalUser(RoleInvite roleInvite) {

        try {
            Map<String, Object> globalArgs = createGlobalArgsForExternalUserInvite(roleInvite);

            Notification notification = new Notification(systemNotificationSource,
                    createUserNotificationTarget(roleInvite),
                    Notifications.INVITE_EXTERNAL_USER, globalArgs);

            ServiceResult<Void> inviteContactEmailSendResult = notificationService.sendNotificationWithFlush(notification, EMAIL);

            inviteContactEmailSendResult.handleSuccessOrFailure(
                    failure -> handleInviteError(roleInvite, failure),
                    success -> handleInviteSuccess(roleInvite)
            );
            return inviteContactEmailSendResult;
        } catch (IllegalArgumentException e) {
            log.error(String.format("Role %s lookup failed for user %s", roleInvite.getEmail(), roleInvite.getTarget().name()), e);
            return ServiceResult.serviceFailure(new Error(CommonFailureKeys.ADMIN_INVALID_USER_ROLE));
        }
    }

    private NotificationTarget createUserNotificationTarget(RoleInvite roleInvite) {
        return new UserNotificationTarget(roleInvite.getName(), roleInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForInternalUserInvite(RoleInvite roleInvite) {
        Map<String, Object> globalArguments = new HashMap<>();
        Role roleResource = roleInvite.getTarget();
        globalArguments.put("role", roleResource.getDisplayName());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + INTERNAL_USER_WEB_CONTEXT, roleInvite));
        return globalArguments;
    }

    private Map<String, Object> createGlobalArgsForExternalUserInvite(RoleInvite roleInvite) {
        Map<String, Object> globalArguments = new HashMap<>();
        Role roleResource = roleInvite.getTarget();
        globalArguments.put("role", roleResource.getDisplayName().toLowerCase());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + EXTERNAL_USER_WEB_CONTEXT, roleInvite));
        return globalArguments;
    }

    private String getInviteUrl(String baseUrl, RoleInvite inviteResource) {
        return String.format("%s/%s/%s", baseUrl, inviteResource.getHash(), "register");
    }

    @Override
    public ServiceResult<RoleInviteResource> getInvite(String inviteHash) {
        RoleInvite roleInvite = roleInviteRepository.getByHash(inviteHash);
        return serviceSuccess(roleInviteMapper.mapToResource(roleInvite));
    }

    @Override
    public ServiceResult<Boolean> checkExistingUser(String inviteHash) {
        return getByHash(inviteHash)
                .andOnSuccessReturn(i -> userRepository.findByEmail(i.getEmail()))
                .andOnSuccess(u -> serviceSuccess(u.isPresent()));
    }

    private ServiceResult<RoleInvite> getByHash(String hash) {
        return find(roleInviteRepository.getByHash(hash), notFoundError(RoleInvite.class, hash));
    }

    private ServiceResult<Boolean> handleInviteError(RoleInvite i, ServiceFailure failure) {
        log.error(String.format("Invite failed %s, %s, %s (error count: %s)", i.getId(), i.getEmail(), i.getTarget().name(), failure.getErrors().size()));
        List<Error> errors = failure.getErrors();
        return serviceFailure(errors);
    }

    private boolean handleInviteSuccess(RoleInvite roleInvite) {
        roleInviteRepository.save(roleInvite.sendOrResend(loggedInUserSupplier.get(), ZonedDateTime.now()));
        return true;
    }

    @Override
    public ServiceResult<RoleInvitePageResource> findPendingInternalUserInvites(String filter, Pageable pageable) {
        Page<RoleInvite> pagedResult = roleInviteRepository.findByEmailContainsAndStatus(filter, InviteStatus.SENT, pageable);

        List<RoleInviteResource> roleInviteResources = pagedResult.getContent()
                .stream()
                .map(roleInviteMapper::mapToResource)
                .collect(Collectors.toList());
        return serviceSuccess(new RoleInvitePageResource(pagedResult.getTotalElements(), pagedResult.getTotalPages(), roleInviteResources, pagedResult.getNumber(), pagedResult.getSize()));
    }

    @Override
    public ServiceResult<List<ExternalInviteResource>> findExternalInvites(String searchString, SearchCategory searchCategory) {
        String searchStringExpr = "%" + StringUtils.trim(searchString) + "%";
        return validateSearchString(searchString).andOnSuccess(() ->
                find(() -> findApplicationInvitesBySearchCriteria(searchStringExpr, searchCategory), () -> findProjectInvitesBySearchCriteria(searchStringExpr, searchCategory))
                        .andOnSuccess((appInvites, prjInvites) ->
                                serviceSuccess(sortByEmail(Stream.concat(
                                        getApplicationInvitesAsExternalInviteResource(appInvites).stream(),
                                        getProjectInvitesAsExternalInviteResource(prjInvites).stream()).collect(Collectors.toList())))
                        )
        );
    }

    @Override
    public ServiceResult<Void> resendInvite(long inviteId) {
        return findRoleInvite(inviteId)
                .andOnSuccess(invite -> {
                    if (externalRolesToInvite().contains(invite.getTarget())) {
                        return inviteExternalUser(invite);
                    } else if (internalRoles().contains(invite.getTarget())) {
                        return inviteInternalUser(invite);
                    } else {
                        return serviceFailure(NOT_AN_INTERNAL_USER_ROLE);
                    }
                });
    }

    @Override
    public ServiceResult<List<RoleInviteResource>> findExternalInvitesByEmail(UserResource user) {
        return serviceSuccess(roleInviteRepository.getByUserId(user.getId()).stream().map(roleInviteMapper::mapToResource).collect(Collectors.toList()));
    }

    private ServiceResult<RoleInvite> findRoleInvite(long inviteId) {
        return find(roleInviteRepository.findById(inviteId), notFoundError(RoleInvite.class, inviteId));
    }

    private ServiceResult<Void> validateSearchString(String searchString) {

        searchString = StringUtils.trim(searchString);

        if (StringUtils.isEmpty(searchString) || StringUtils.length(searchString) < 3) {
            return serviceFailure(new Error(USER_SEARCH_INVALID_INPUT_LENGTH, singletonList(3)) );
        } else {
            return serviceSuccess();
        }
    }

    private ServiceResult<List<ApplicationInvite>> findApplicationInvitesBySearchCriteria(String searchString, SearchCategory searchCategory ) {
            List<ApplicationInvite> applicationInvites;
            switch (searchCategory) {
                case NAME:
                    applicationInvites = applicationInviteRepository.findByNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;

                case ORGANISATION_NAME:
                    applicationInvites = applicationInviteRepository.findByInviteOrganisationOrganisationNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;

                case EMAIL:
                default:
                    applicationInvites = applicationInviteRepository.findByEmailLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;
            }
            return serviceSuccess(applicationInvites);

    }

    private ServiceResult<List<ProjectUserInvite>> findProjectInvitesBySearchCriteria(String searchString, SearchCategory searchCategory ) {
            final List<ProjectUserInvite> projectInvites;
            switch (searchCategory) {
                case NAME:
                    projectInvites = projectUserInviteRepository.findByNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;

                case ORGANISATION_NAME:
                    projectInvites = projectUserInviteRepository.findByOrganisationNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;

                case EMAIL:
                default:
                    projectInvites = projectUserInviteRepository.findByEmailLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;
            }
            return serviceSuccess(projectInvites);

    }

    private List<ExternalInviteResource> getApplicationInvitesAsExternalInviteResource(List<ApplicationInvite> appInvites){

        return appInvites.stream().map(appInvite -> new ExternalInviteResource(
                appInvite.getName(),
                appInvite.getInviteOrganisation().getOrganisation() != null ? appInvite.getInviteOrganisation().getOrganisation().getName() : appInvite.getInviteOrganisation().getOrganisationName(), // organisation may not exist yet (new collaborator)
                appInvite.getInviteOrganisation().getOrganisation() != null ? appInvite.getInviteOrganisation().getOrganisation().getId().toString() : "New",
                appInvite.getEmail(),
                appInvite.getTarget().getId(),
                appInvite.getStatus())).collect(Collectors.toList());
    }

    private List<ExternalInviteResource> getProjectInvitesAsExternalInviteResource(List<ProjectUserInvite> prjInvites){

        return prjInvites.stream().map(projectInvite ->
                new ExternalInviteResource(
                        projectInvite.getName(),
                        projectInvite.getOrganisation().getName(),
                        projectInvite.getOrganisation().getId().toString(),
                        projectInvite.getEmail(),
                        projectInvite.getTarget().getApplication().getId(),
                        projectInvite.getStatus())).collect(Collectors.toList());
    }

    private List<ExternalInviteResource> sortByEmail(List<ExternalInviteResource> extInviteResources) {
        return extInviteResources.stream().sorted(Comparator.comparing(extInviteResource -> extInviteResource.getEmail().toUpperCase())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveAssessorInvite(UserResource invitedUser, Role role, Long innovationAreaId) {
            return   validateUserRoleInvite(invitedUser, role)
                    .andOnSuccess(() -> validateUserEmailAvailable(invitedUser))
                    .andOnSuccess(() -> validateUserNotAlreadyInvited(invitedUser))
                    .andOnSuccess(() -> saveAssessorRoleInvite(invitedUser, role, innovationAreaId))
                    .andOnSuccess(this::inviteExternalUser);
    }

    private ServiceResult<Void> validateUserRoleInvite(UserResource invitedUser, Role role) {
        if (StringUtils.isEmpty(invitedUser.getEmail()) || StringUtils.isEmpty(invitedUser.getFirstName())
                || StringUtils.isEmpty(invitedUser.getLastName()) || role == null){
            return serviceFailure(USER_ROLE_INVITE_INVALID);
        }
       return serviceSuccess();
    }

    private ServiceResult<RoleInvite> saveAssessorRoleInvite(UserResource invitedUser, Role role, Long innovationAreaId) {
        RoleInvite roleInvite = new RoleInvite(invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                role,
                CREATED,
                null);

        roleInvite.setInnovationArea(getInnovationArea(innovationAreaId).getSuccess());
        RoleInvite invite = roleInviteRepository.save(roleInvite);

        return serviceSuccess(invite);
    }

    private ServiceResult<InnovationArea> getInnovationArea(long innovationCategoryId) {
        return find(innovationAreaRepository.findById(innovationCategoryId), notFoundError(Category.class, innovationCategoryId, INNOVATION_AREA));
    }
}