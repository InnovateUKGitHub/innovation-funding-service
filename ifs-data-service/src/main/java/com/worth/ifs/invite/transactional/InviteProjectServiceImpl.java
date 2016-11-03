package com.worth.ifs.invite.transactional;


import com.google.common.collect.Lists;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.mapper.InviteProjectMapper;
import com.worth.ifs.invite.repository.InviteProjectRepository;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.project.transactional.ProjectService;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class InviteProjectServiceImpl extends BaseTransactionalService implements InviteProjectService {

    private static final Log LOG = LogFactory.getLog(InviteServiceImpl.class);

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private InviteProjectMapper inviteMapper;

    @Autowired
    private InviteProjectRepository inviteProjectRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    LocalValidatorFactoryBean validator;


    public InviteProjectServiceImpl() {
        validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();
    }

    @Override
    public ServiceResult<Void> saveProjectInvite(@P("inviteProjectResource") InviteProjectResource inviteProjectResource) {

        return validateProjectInviteResource(inviteProjectResource).andOnSuccess(() ->
               validateUserNotAlreadyInvited(inviteProjectResource).andOnSuccess(() ->
               validateTargetUserIsValid(inviteProjectResource).andOnSuccess(() -> {

            ProjectInvite projectInvite = inviteMapper.mapToDomain(inviteProjectResource);
            Errors errors = new BeanPropertyBindingResult(projectInvite, projectInvite.getClass().getName());
            validator.validate(projectInvite, errors);
            if (errors.hasErrors()) {
                errors.getFieldErrors().stream().peek(e -> LOG.debug(format("Field error: %s ", e.getField())));
                return serviceFailure(badRequestError(errors.toString()));
            } else {
                projectInvite.generateHash();
                inviteProjectRepository.save(projectInvite);
                return serviceSuccess();
            }
        })));
    }

    @Override
    public ServiceResult<InviteProjectResource> getInviteByHash(String hash) {
        return getByHash(hash).andOnSuccessReturn(inviteMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<InviteProjectResource>> getInvitesByProject(Long projectId) {
        if(projectId == null) {
            return serviceFailure(new Error(PROJECT_INVITE_INVALID_PROJECT_ID, NOT_FOUND));
        }
        List<ProjectInvite> invites = inviteProjectRepository.findByProjectId(projectId);
        return serviceSuccess(Lists.newArrayList(inviteMapper.mapToResource(invites)));
    }

    @Override
    public ServiceResult<Void> acceptProjectInvite(String inviteHash, Long userId) {
        return find(invite(inviteHash), user(userId)).andOnSuccess((invite, user) -> {
            if(invite.getEmail().equalsIgnoreCase(user.getEmail())){
                ProjectInvite projectInvite = inviteProjectRepository.save(invite.open());
                return projectService.addPartner(projectInvite.getTarget().getId(), user.getId(), projectInvite.getOrganisation().getId()).andOnSuccess(pu -> {
                    pu.setInvite(projectInvite);
                    projectUserRepository.save(pu.accept());
                    return serviceSuccess();
                });
            }
            LOG.error(format("Invited email address not the same as the users email address %s => %s ", user.getEmail(), invite.getEmail()));
            Error e = new Error("Invited email address not the same as the users email address", HttpStatus.NOT_ACCEPTABLE);
            return serviceFailure(e);
        });
    }

    @Override
    public ServiceResult<Boolean> checkUserExistingByInviteHash(@P("hash") String hash) {
        return getByHash(hash)
                .andOnSuccessReturn(i -> userRepository.findByEmail(i.getEmail()))
                .andOnSuccess(u -> serviceSuccess(u.isPresent()));
    }

    @Override
    public ServiceResult<UserResource> getUserByInviteHash(@P("hash") String hash) {
        return getByHash(hash)
                .andOnSuccessReturn(i -> userRepository.findByEmail(i.getEmail()).map(userMapper::mapToResource))
                .andOnSuccess(u -> u.isPresent() ?
                        serviceSuccess(u.get()) :
                        serviceFailure(notFoundError(UserResource.class)));
    }

    private ServiceResult<Void> validateProjectInviteResource(InviteProjectResource inviteProjectResource) {

        if (StringUtils.isEmpty(inviteProjectResource.getEmail()) || StringUtils.isEmpty(inviteProjectResource.getName())
                || inviteProjectResource.getProject() == null ||inviteProjectResource.getOrganisation() == null ){
            return serviceFailure(badRequestError("PROJECT_INVITE_INVALID"));
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> validateUserNotAlreadyInvited(InviteProjectResource invite) {

        List<ProjectInvite> existingInvites = inviteProjectRepository.findByProjectIdAndEmail(invite.getProject(), invite.getEmail());
        return existingInvites.isEmpty() ? serviceSuccess() : serviceFailure(PROJECT_SETUP_INVITE_TARGET_USER_ALREADY_INVITED_ON_PROJECT);
    }

    private ServiceResult<Void> validateTargetUserIsValid(InviteProjectResource invite) {

        String targetEmail = invite.getEmail();

        Optional<User> existingUser = userRepository.findByEmail(targetEmail);

        return existingUser.map(user ->
               validateUserIsInSameOrganisation(invite, user).andOnSuccess(() ->
               validateUserIsNotAlreadyPartnerInOrganisation(invite, user))).
               orElse(serviceSuccess());
    }

    private ServiceResult<Void> validateUserIsInSameOrganisation(InviteProjectResource invite, User user) {

        List<Organisation> usersOrganisations = user.getOrganisations();

        if (!simpleMap(usersOrganisations, Organisation::getId).contains(invite.getOrganisation())) {
            return serviceFailure(PROJECT_SETUP_INVITE_TARGET_USER_NOT_IN_CORRECT_ORGANISATION);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> validateUserIsNotAlreadyPartnerInOrganisation(InviteProjectResource invite, User user) {

        ProjectUser existingUserEntryForOrganisation = projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(invite.getProject(), invite.getOrganisation(), user.getId(), PROJECT_PARTNER);

        return existingUserEntryForOrganisation == null ? serviceSuccess() :
                serviceFailure(PROJECT_SETUP_INVITE_TARGET_USER_ALREADY_EXISTS_ON_PROJECT);
    }

    private Supplier<ServiceResult<ProjectInvite>> invite(final String hash) {
        return () -> getByHash(hash);
    }

    private ServiceResult<ProjectInvite> getByHash(String hash) {
        return find(inviteProjectRepository.getByHash(hash), notFoundError(ProjectInvite.class, hash));
    }
}
