package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.invite.mapper.ApplicationKtaInviteMapper;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ApplicationKtaInviteServiceImpl extends InviteService<ApplicationKtaInvite> implements ApplicationKtaInviteService {

    private static final String EDIT_EMAIL_FIELD = "ktaEmail";

    @Autowired
    private UserService userService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private ApplicationKtaInviteMapper applicationKtaInviteMapper;

    @Autowired
    private ApplicationKtaInviteRepository applicationKtaInviteRepository;

    @Autowired
    private ApplicationInviteNotificationService applicationInviteNotificationService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    public ServiceResult<ApplicationKtaInviteResource> getKtaInviteByApplication(long applicationId) {
        return serviceSuccess(
                    applicationKtaInviteMapper.mapToResource(applicationKtaInviteRepository.findByApplicationId(applicationId).orElse(null))
            );
    }

    @Override
    public ServiceResult<Void> resendKtaInvite(ApplicationKtaInviteResource inviteResource) {
        ApplicationKtaInvite invite = applicationKtaInviteMapper.mapToDomain(inviteResource);
        invite.send(loggedInUserSupplier.get(), now());
        applicationKtaInviteRepository.save(invite);
        return applicationInviteNotificationService.resendKtaInvite(invite);
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeKtaInviteByApplication(long applicationId) {
        Optional<ApplicationKtaInvite> invite = applicationKtaInviteRepository.findByApplicationId(applicationId);
        Application application;
        if (invite.isPresent()) {
            removeKtaInvite(invite.get());
            application = invite.get().getTarget();
        } else {
            Optional<Application> maybeApplication = applicationRepository.findById(applicationId);
            if (maybeApplication.isPresent()) {
                application = maybeApplication.get();
            } else {
                return serviceFailure(notFoundError(ApplicationResource.class));
            }
        }

        List<ProcessRole> rolesToRemove = application.getProcessRoles().stream().filter(pr -> pr.getRole().isKta()).collect(Collectors.toList());
        if (!rolesToRemove.isEmpty()) {
            processRoleRepository.deleteAll(rolesToRemove);
        }
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveKtaInvite(ApplicationKtaInviteResource inviteResource) {
        return validateKtaApplication(inviteResource, EDIT_EMAIL_FIELD).andOnSuccess(validated -> {
            ApplicationKtaInvite invite = mapKtaInviteResourceToKtaInvite(inviteResource);
            applicationKtaInviteRepository.save(invite);
            return applicationInviteNotificationService.inviteKta(invite);
        });
    }

    private void removeKtaInvite(ApplicationKtaInvite applicationKtaInvite) {
        applicationKtaInviteRepository.delete(applicationKtaInvite);
        applicationInviteNotificationService.removeKtaFromApplication(applicationKtaInvite);
    }

    private ApplicationKtaInvite mapKtaInviteResourceToKtaInvite(ApplicationKtaInviteResource inviteResource) {
        Application application = applicationRepository.findById(inviteResource.getApplication()).orElse(null);
        return new ApplicationKtaInvite(inviteResource.getName(), inviteResource.getEmail(), application, null, InviteStatus.CREATED);
    }

    private ServiceResult<ApplicationKtaInviteResource> validateKtaApplication(ApplicationKtaInviteResource inviteResource, String errorField) {
        Optional<ApplicationKtaInvite> existing = applicationKtaInviteRepository.findByApplicationId(inviteResource.getApplication());
        if (existing.isPresent()) {
            return serviceFailure(fieldError(errorField, inviteResource.getEmail(), "kta.already.invited"));
        }
        ServiceResult<UserResource> userResult = userService.findByEmail(inviteResource.getEmail());
        if (userResult.isFailure() || !userResult.getSuccess().hasRole(Role.KNOWLEDGE_TRANSFER_ADVISER)) {
            return serviceFailure(fieldError(errorField, inviteResource.getEmail(), "user.not.registered.kta"));
        }
        inviteResource.setName(userResult.getSuccess().getName());
        return serviceSuccess(inviteResource);
    }

    @Override
    public ServiceResult<ApplicationKtaInviteResource> getKtaInviteByHash(String hash) {
        return getByHash(hash)
                .andOnSuccessReturn(this::mapInviteToKtaInviteResource);
    }

    private ApplicationKtaInviteResource mapInviteToKtaInviteResource(ApplicationKtaInvite applicationKtaInvite) {
        ApplicationKtaInviteResource ktaInviteResource = applicationKtaInviteMapper.mapToResource(applicationKtaInvite);
        Organisation leadOrganisation = organisationRepository.findById(ktaInviteResource.getLeadOrganisationId()).get();
        ktaInviteResource.setLeadOrganisationName(leadOrganisation.getName());
        ktaInviteResource.setHash(applicationKtaInvite.getHash());
        return ktaInviteResource;
    }

    @Override
    @Transactional
    public ServiceResult<Void> acceptInvite(String hash) {
        return getByHash(hash)
                .andOnSuccess(invite -> {
                    applicationKtaInviteRepository.save(invite.open());
                    ProcessRole ktaProcessRole = new ProcessRole(invite.getUser(), invite.getTarget().getId(), ProcessRoleType.KNOWLEDGE_TRANSFER_ADVISER);
                    processRoleRepository.save(ktaProcessRole);
                    return serviceSuccess();

                });
    }

    @Override
    protected Class<ApplicationKtaInvite> getInviteClass() {
        return ApplicationKtaInvite.class;
    }

    @Override
    protected InviteRepository<ApplicationKtaInvite> getInviteRepository() {
        return applicationKtaInviteRepository;
    }
}
