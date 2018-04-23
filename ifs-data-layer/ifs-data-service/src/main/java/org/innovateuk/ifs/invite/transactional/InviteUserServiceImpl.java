package org.innovateuk.ifs.invite.transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.mapper.RoleInviteMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ProjectInviteRepository;
import org.innovateuk.ifs.invite.repository.RoleInviteRepository;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.project.transactional.EmailService;
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
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service implementation providing operations around invites for users.
 */
@Service
public class InviteUserServiceImpl extends BaseTransactionalService implements InviteUserService {

    @Autowired
    private RoleInviteRepository roleInviteRepository;

    @Autowired
    private ProjectInviteRepository projectInviteRepository;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private RoleInviteMapper roleInviteMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    private static final Log LOG = LogFactory.getLog(InviteUserServiceImpl.class);

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    public static final String WEB_CONTEXT = "/management/registration";

    enum Notifications {
        INVITE_INTERNAL_USER
    }

    private static final String DEFAULT_INTERNAL_USER_EMAIL_DOMAIN = "innovateuk.gov.uk";

    @Value("${ifs.system.internal.user.email.domain}")
    private String internalUserEmailDomain;

    @Override
    @Transactional
    public ServiceResult<Void> saveUserInvite(UserResource invitedUser, Role adminRoleType) {

        return validateInvite(invitedUser, adminRoleType)
                .andOnSuccess(() -> validateInternalUserRole(adminRoleType))
                .andOnSuccess(() -> validateEmail(invitedUser.getEmail()))
                .andOnSuccess(() -> validateUserEmailAvailable(invitedUser))
                .andOnSuccess(() -> validateUserNotAlreadyInvited(invitedUser))
                .andOnSuccess(role -> saveInvite(invitedUser, adminRoleType))
                .andOnSuccess(this::inviteInternalUser);
    }

    private ServiceResult<Void> validateInvite(UserResource invitedUser, Role adminRoleType) {

        if (StringUtils.isEmpty(invitedUser.getEmail()) || StringUtils.isEmpty(invitedUser.getFirstName())
                || StringUtils.isEmpty(invitedUser.getLastName()) || adminRoleType == null){
            return serviceFailure(USER_ROLE_INVITE_INVALID);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> validateInternalUserRole(Role userRoleType) {

        return Role.internalRoles().stream().anyMatch(internalRole -> internalRole == userRoleType)
                ? serviceSuccess() : serviceFailure(NOT_AN_INTERNAL_USER_ROLE);
    }

    private ServiceResult<Void> validateEmail(String email) {

        internalUserEmailDomain = StringUtils.defaultIfBlank(internalUserEmailDomain, DEFAULT_INTERNAL_USER_EMAIL_DOMAIN);

        String domain = StringUtils.substringAfter(email, "@");

        if (!internalUserEmailDomain.equalsIgnoreCase(domain)) {
            return serviceFailure(USER_ROLE_INVITE_INVALID_EMAIL);
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

    private ServiceResult<RoleInvite> saveInvite(UserResource invitedUser, Role role) {
        RoleInvite roleInvite = new RoleInvite(invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                role,
                CREATED);

        RoleInvite invite = roleInviteRepository.save(roleInvite);

        return serviceSuccess(invite);
    }

    private ServiceResult<Void> inviteInternalUser(RoleInvite roleInvite) {

        try {
            Map<String, Object> globalArgs = createGlobalArgsForInternalUserInvite(roleInvite);

            ServiceResult<Void> inviteContactEmailSendResult = emailService.sendEmail(
                    singletonList(createInviteInternalUserNotificationTarget(roleInvite)),
                    globalArgs,
                    Notifications.INVITE_INTERNAL_USER);

            inviteContactEmailSendResult.handleSuccessOrFailure(
                    failure -> handleInviteError(roleInvite, failure),
                    success -> handleInviteSuccess(roleInvite)
            );
            return inviteContactEmailSendResult;
        } catch (IllegalArgumentException e) {
            LOG.error(String.format("Role %s lookup failed for user %s", roleInvite.getEmail(), roleInvite.getTarget().getName()));
            return ServiceResult.serviceFailure(new Error(CommonFailureKeys.ADMIN_INVALID_USER_ROLE));
        }
    }

    private NotificationTarget createInviteInternalUserNotificationTarget(RoleInvite roleInvite) {
        return new UserNotificationTarget(roleInvite.getName(), roleInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForInternalUserInvite(RoleInvite roleInvite) {
        Map<String, Object> globalArguments = new HashMap<>();
        Role roleResource = roleInvite.getTarget();
        globalArguments.put("role", roleResource.getDisplayName());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, roleInvite));
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
        LOG.error(String.format("Invite failed %s, %s, %s (error count: %s)", i.getId(), i.getEmail(), i.getTarget().getName(), failure.getErrors().size()));
        List<Error> errors = failure.getErrors();
        return serviceFailure(errors);
    }

    private boolean handleInviteSuccess(RoleInvite roleInvite) {
        roleInviteRepository.save(roleInvite.sendOrResend(loggedInUserSupplier.get(), ZonedDateTime.now()));
        return true;
    }

    @Override
    public ServiceResult<RoleInvitePageResource> findPendingInternalUserInvites(Pageable pageable) {
        Page<RoleInvite> pagedResult = roleInviteRepository.findByStatus(InviteStatus.SENT, pageable);
        List<RoleInviteResource> roleInviteResources = simpleMap(pagedResult.getContent(), roleInvite -> roleInviteMapper.mapToResource(roleInvite));
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
    public ServiceResult<Void> resendInternalUserInvite(long inviteId) {
        return findRoleInvite(inviteId).andOnSuccess(this::inviteInternalUser);
    }

    private ServiceResult<RoleInvite> findRoleInvite(long inviteId) {
        return find(ofNullable(roleInviteRepository.findOne(inviteId)), notFoundError(RoleInvite.class, inviteId));
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

    private ServiceResult<List<ProjectInvite>> findProjectInvitesBySearchCriteria(String searchString, SearchCategory searchCategory ) {
            List<ProjectInvite> projectInvites;
            switch (searchCategory) {
                case NAME:
                    projectInvites = projectInviteRepository.findByNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;

                case ORGANISATION_NAME:
                    projectInvites = projectInviteRepository.findByOrganisationNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;

                case EMAIL:
                default:
                    projectInvites = projectInviteRepository.findByEmailLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
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

    private List<ExternalInviteResource> getProjectInvitesAsExternalInviteResource(List<ProjectInvite> prjInvites){

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
}