package com.worth.ifs.invite.transactional;


import com.google.common.collect.Lists;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceFailure;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.mapper.InviteOrganisationMapper;
import com.worth.ifs.invite.mapper.InviteProjectMapper;
import com.worth.ifs.invite.repository.InviteProjectRepository;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.notifications.resource.*;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
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

import java.util.*;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.*;

import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID_PROJECT_ID;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;

import static com.worth.ifs.util.EntityLookupCallbacks.find;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class InviteProjectServiceImpl extends BaseTransactionalService implements InviteProjectService {

    private static final Log LOG = LogFactory.getLog(InviteServiceImpl.class);

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private InviteProjectMapper inviteMapper;

    @Autowired
    private InviteOrganisationMapper inviteOrganisationMapper;

    @Autowired
    private InviteProjectRepository inviteProjectRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    LocalValidatorFactoryBean validator;


    public InviteProjectServiceImpl() {
        validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();
    }

    @Override
    public ServiceResult<Void> saveFinanceContactInvite(@P("inviteProjectResource") InviteProjectResource inviteProjectResource) {

        if (inviteProjectResourceIsValid(inviteProjectResource)) {
            ProjectInvite invite = inviteMapper.mapToDomain(inviteProjectResource);
            Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
            validator.validate(invite, errors);
            if (errors.hasErrors()) {
                errors.getFieldErrors().stream().peek(e -> LOG.debug(String.format("Field error: %s ", e.getField())));
                return serviceFailure(badRequestError(errors.toString()));

            } else{

                Project project = projectRepository.findOne(inviteProjectResource.getProject());
                Role partnerRole = roleRepository.findOneByName(PARTNER.getName());
                final ProjectUser projectUser = projectUserRepository.findByProjectIdAndRoleIdAndUserId(project.getId(),
                        partnerRole.getId(), inviteProjectResource.getUser());
                Organisation organisation = organisationRepository.findOne(projectUser.getOrganisation().getId());
                String hashCode = invite.generateHash();
                ProjectInvite projectInvite = new ProjectInvite(inviteProjectResource.getName(),inviteProjectResource.getEmail(),
                        hashCode, organisation, project );

                inviteProjectRepository.save(projectInvite);
                return serviceSuccess();
            }
        }
        return serviceFailure(badRequestError("The Invite is not valid"));

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
        LOG.error(String.format("acceptInvite %s => %s ", inviteHash, userId));
        return find(invite(inviteHash), user(userId)).andOnSuccess((invite, user) -> {

            if(invite.getEmail().equalsIgnoreCase(user.getEmail())){
                invite.setStatus(InviteStatusConstants.ACCEPTED);

                invite = inviteProjectRepository.save(invite);
                // need to check with business if reqquired to save in Project User entity

                return serviceSuccess();
            }
            LOG.error(String.format("Invited emailaddress not the same as the users emailaddress %s => %s ", user.getEmail(), invite.getEmail()));
            Error e = new Error("Invited emailaddress not the same as the users emailaddress", HttpStatus.NOT_ACCEPTABLE);
            return serviceFailure(e);
        });
    }


    @Override
    public ServiceResult<Void> checkUserExistingByInviteHash(@P("hash") String hash) {
        return getByHash(hash)
                .andOnSuccessReturn(i -> userRepository.findByEmail(i.getEmail()))
                .andOnSuccess(u -> {
                    if(u.isPresent()){
                        return serviceSuccess();
                    }else{
                        return serviceFailure(CommonErrors.notFoundError(ApplicationInvite.class, hash));
                    }
                })
                .andOnSuccessReturnVoid();
    }

    private boolean inviteProjectResourceIsValid(InviteProjectResource inviteProjectResource) {

        if (StringUtils.isEmpty(inviteProjectResource.getEmail()) || StringUtils.isEmpty(inviteProjectResource.getName())
                || inviteProjectResource.getProject() == null ||inviteProjectResource.getOrganisation() == null ){
            return false;
        }
        return true;
    }


    private String getInviteUrl(String baseUrl, ProjectInvite invite) {
        return String.format("%s/accept-invite/%s", baseUrl, invite.getHash());
    }

    private Supplier<ServiceResult<ProjectInvite>> invite(final String hash) {
        return () -> getByHash(hash);
    }

    private ServiceResult<ProjectInvite> getByHash(String hash) {
        return find(inviteProjectRepository.getByHash(hash), notFoundError(ProjectInvite.class, hash));
    }
}
