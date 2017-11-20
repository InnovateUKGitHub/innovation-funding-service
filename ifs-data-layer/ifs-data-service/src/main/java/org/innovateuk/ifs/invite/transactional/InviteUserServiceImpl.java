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
import org.innovateuk.ifs.invite.repository.InviteProjectRepository;
import org.innovateuk.ifs.invite.repository.InviteRoleRepository;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.project.transactional.EmailService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.mapper.RoleMapper;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service implementation providing operations around invites for users.
 */
@Service
public class InviteUserServiceImpl extends BaseTransactionalService implements InviteUserService {

    @Autowired
    private InviteRoleRepository inviteRoleRepository;

    @Autowired
    private InviteProjectRepository inviteProjectRepository;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private RoleInviteMapper roleInviteMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private RoleMapper roleMapper;

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
    public ServiceResult<Void> saveUserInvite(UserResource invitedUser, UserRoleType adminRoleType) {

        return validateInvite(invitedUser, adminRoleType)
                .andOnSuccess(() -> validateInternalUserRole(adminRoleType))
                .andOnSuccess(() -> validateEmail(invitedUser.getEmail()))
                .andOnSuccess(() -> validateUserEmailAvailable(invitedUser))
                .andOnSuccess(() -> validateUserNotAlreadyInvited(invitedUser))
                .andOnSuccess(() -> getRole(adminRoleType))
                .andOnSuccess(role -> saveInvite(invitedUser, role))
                .andOnSuccess(this::inviteInternalUser);
    }

    private ServiceResult<Void> validateInvite(UserResource invitedUser, UserRoleType adminRoleType) {

        if (StringUtils.isEmpty(invitedUser.getEmail()) || StringUtils.isEmpty(invitedUser.getFirstName())
                || StringUtils.isEmpty(invitedUser.getLastName()) || adminRoleType == null){
            return serviceFailure(USER_ROLE_INVITE_INVALID);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> validateInternalUserRole(UserRoleType userRoleType) {

        return UserRoleType.internalRoles().stream().anyMatch(internalRole -> internalRole.equals(userRoleType))?
                serviceSuccess() : serviceFailure(NOT_AN_INTERNAL_USER_ROLE);
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

        List<RoleInvite> existingInvites = inviteRoleRepository.findByEmail(invitedUser.getEmail());
        return existingInvites.isEmpty() ? serviceSuccess() : serviceFailure(USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED);
    }

    private ServiceResult<RoleInvite> saveInvite(UserResource invitedUser, Role role) {
        RoleInvite roleInvite = new RoleInvite(invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                role,
                CREATED);

        RoleInvite invite = inviteRoleRepository.save(roleInvite);

        return serviceSuccess(invite);
    }

    private ServiceResult<Void> inviteInternalUser(RoleInvite roleInvite) {

        try {
            Map<String, Object> globalArgs = createGlobalArgsForInternalUserInvite(roleInvite);
            ServiceResult<Void> inviteContactEmailSendResult = emailService.sendEmail(
                    Collections.singletonList(createInviteInternalUserNotificationTarget(roleInvite)),
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
        return new ExternalUserNotificationTarget(roleInvite.getName(), roleInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForInternalUserInvite(RoleInvite roleInvite) {
        Map<String, Object> globalArguments = new HashMap<>();
        RoleResource roleResource = roleMapper.mapIdToResource(roleInvite.getTarget().getId());
        globalArguments.put("role", roleResource.getDisplayName());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, roleInvite));
        return globalArguments;
    }

    private String getInviteUrl(String baseUrl, RoleInvite inviteResource) {
        return String.format("%s/%s/%s", baseUrl, inviteResource.getHash(), "register");
    }

    @Override
    public ServiceResult<RoleInviteResource> getInvite(String inviteHash) {
        RoleInvite roleInvite = inviteRoleRepository.getByHash(inviteHash);
        return serviceSuccess(roleInviteMapper.mapToResource(roleInvite));
    }

    @Override
    public ServiceResult<Boolean> checkExistingUser(String inviteHash) {
        return getByHash(inviteHash)
                .andOnSuccessReturn(i -> userRepository.findByEmail(i.getEmail()))
                .andOnSuccess(u -> serviceSuccess(u.isPresent()));
    }

    private ServiceResult<RoleInvite> getByHash(String hash) {
        return find(inviteRoleRepository.getByHash(hash), notFoundError(RoleInvite.class, hash));
    }

    private ServiceResult<Boolean> handleInviteError(RoleInvite i, ServiceFailure failure) {
        LOG.error(String.format("Invite failed %s, %s, %s (error count: %s)", i.getId(), i.getEmail(), i.getTarget().getName(), failure.getErrors().size()));
        List<Error> errors = failure.getErrors();
        return serviceFailure(errors);
    }

    private boolean handleInviteSuccess(RoleInvite roleInvite) {
        inviteRoleRepository.save(roleInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
        return true;
    }

    @Override
    public ServiceResult<RoleInvitePageResource> findPendingInternalUserInvites(Pageable pageable) {
        Page<RoleInvite> pagedResult = inviteRoleRepository.findByStatus(InviteStatus.SENT, pageable);
        List<RoleInviteResource> roleInviteResources = simpleMap(pagedResult.getContent(), roleInvite -> roleInviteMapper.mapToResource(roleInvite));
        return serviceSuccess(new RoleInvitePageResource(pagedResult.getTotalElements(), pagedResult.getTotalPages(), sortByName(roleInviteResources), pagedResult.getNumber(), pagedResult.getSize()));
    }

    private List<RoleInviteResource> sortByName(List<RoleInviteResource> roleInviteResources) {
        return roleInviteResources.stream().sorted(Comparator.comparing(roleInviteResource -> roleInviteResource.getName().toUpperCase())).collect(Collectors.toList());
    }

    @Override
    public ServiceResult<List<ExternalInviteResource>> findExternalInvites(String searchString, SearchCategory searchCategory) {
        return  validateSearchString(searchString)
                .andOnSuccess(() ->
                    find(() -> findApplicationInvitesBySearchCriteria(searchString, searchCategory), () -> findProjectInvitesBySearchCriteria(searchString, searchCategory))
                    .andOnSuccess((appInvites, prjInvites) ->
                            serviceSuccess(sortByEmail(Stream.concat(
                                    getApplicationInvitesAsExternalInviteResource(appInvites).stream(),
                                    getProjectInvitesAsExternalInviteResource(prjInvites).stream()).collect(Collectors.toList())))
                ));
    }

    private ServiceResult<Void> validateSearchString(String searchString) {

        searchString = StringUtils.trim(searchString);

        if (StringUtils.isEmpty(searchString) || StringUtils.length(searchString) < 5) {
            return serviceFailure(CommonFailureKeys.GENERAL_INVALID_ARGUMENT);
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
                    projectInvites = inviteProjectRepository.findByNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;

                case ORGANISATION_NAME:
                    projectInvites = inviteProjectRepository.findByOrganisationNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;

                case EMAIL:
                default:
                    projectInvites = inviteProjectRepository.findByEmailLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
                    break;
            }
            return serviceSuccess(projectInvites);

    }

    private List<ExternalInviteResource> getApplicationInvitesAsExternalInviteResource(List<ApplicationInvite> appInvites){

        //appInvites = filterApplicationInvites(appInvites, searchString, searchCategory);

        return appInvites.stream().map(appInvite -> new ExternalInviteResource(
                appInvite.getName(),
                appInvite.getInviteOrganisation().getOrganisation() != null ? appInvite.getInviteOrganisation().getOrganisation().getName() : appInvite.getInviteOrganisation().getOrganisationName(), // organisation may not exist yet (new collaborator)
                appInvite.getInviteOrganisation().getOrganisation() != null ? appInvite.getInviteOrganisation().getOrganisation().getId().toString() : "new",
                appInvite.getEmail(),
                appInvite.getTarget().getId(),
                appInvite.getStatus())).collect(Collectors.toList());
    }

/*    private List<ApplicationInvite> filterApplicationInvites(List<ApplicationInvite> appInvites, String searchString, SearchCategory searchCategory){
        switch (searchCategory) {
            case NAME:
                return filterApplicationInvites(appInvites, searchString, applicationInvite -> applicationInvite.getName());

            case ORGANISATION_NAME:
                return filterApplicationInvites(appInvites, searchString, applicationInvite -> applicationInvite.getInviteOrganisation().getOrganisationName());

            case EMAIL:
            default:
                return filterApplicationInvites(appInvites, searchString, applicationInvite -> applicationInvite.getEmail());
        }
    }*/

/*    private List<ApplicationInvite> filterApplicationInvites(List<ApplicationInvite> appInvites, String searchString, Function<ApplicationInvite, String> applicationInviteKeyExtractor) {

        return appInvites.stream().filter(applicationInvite -> applicationInviteKeyExtractor.apply(applicationInvite).toLowerCase().contains(searchString.toLowerCase())).collect(Collectors.toList());

    }*/

    private List<ExternalInviteResource> getProjectInvitesAsExternalInviteResource(List<ProjectInvite> prjInvites){

        //prjInvites = filterProjectInvites(prjInvites, searchString, searchCategory);

        return prjInvites.stream().map(projectInvite ->
                new ExternalInviteResource(
                        projectInvite.getName(),
                        projectInvite.getOrganisation().getName(),
                        projectInvite.getOrganisation().getId().toString(),
                        projectInvite.getEmail(),
                        projectInvite.getTarget().getApplication().getId(),
                        projectInvite.getStatus())).collect(Collectors.toList());
    }

/*    private List<ProjectInvite> filterProjectInvites(List<ProjectInvite> projectInvites, String searchString, SearchCategory searchCategory){
        switch (searchCategory) {
            case NAME:
                return filterProjectInvites(projectInvites, searchString, projectInvite -> projectInvite.getName());

            case ORGANISATION_NAME:
                return filterProjectInvites(projectInvites, searchString, projectInvite -> projectInvite.getOrganisation().getName());

            case EMAIL:
            default:
                return filterProjectInvites(projectInvites, searchString, projectInvite -> projectInvite.getEmail());
        }
    }*/

/*    private List<ProjectInvite> filterProjectInvites(List<ProjectInvite> projectInvites, String searchString, Function<ProjectInvite, String> projectInviteKeyExtractor) {

        return projectInvites.stream().filter(projectInvite -> projectInviteKeyExtractor.apply(projectInvite).toLowerCase().contains(searchString.toLowerCase())).collect(Collectors.toList());

    }*/

    private List<ExternalInviteResource> sortByEmail(List<ExternalInviteResource> extInviteResources) {
        return extInviteResources.stream().sorted(Comparator.comparing(extInviteResource -> extInviteResource.getEmail().toUpperCase())).collect(Collectors.toList());
    }
}