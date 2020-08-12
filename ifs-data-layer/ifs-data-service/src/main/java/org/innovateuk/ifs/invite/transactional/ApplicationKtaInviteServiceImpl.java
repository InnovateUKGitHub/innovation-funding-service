package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.invite.mapper.ApplicationKtaInviteMapper;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public class ApplicationKtaInviteServiceImpl extends InviteService<ApplicationKtaInvite> implements ApplicationKtaInviteService {

    private static final String EDIT_EMAIL_FIELD = "stagedInvite.email";

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

    @Override
    public ServiceResult<List<ApplicationKtaInviteResource>> getKtaInvitesByApplication(Long applicationId) {
        return serviceSuccess(
                simpleMap(
                        applicationKtaInviteRepository.findByApplicationId(applicationId),
                        applicationKtaInviteMapper::mapToResource
                )
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
    public ServiceResult<Void> removeKtaApplicationInvite(long ktaInviteResourceId) {
        return find(applicationKtaInviteMapper.mapIdToDomain(ktaInviteResourceId), notFoundError(ApplicationKtaInvite.class))
                .andOnSuccessReturnVoid(this::removeKtaInvite);
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveKtaInvite(ApplicationKtaInviteResource inviteResource) {
        return validateKtaApplication(inviteResource, EDIT_EMAIL_FIELD).andOnSuccess(() -> {
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
        return new ApplicationKtaInvite(inviteResource.getEmail(), application, null, InviteStatus.CREATED);
    }

    private ServiceResult<Void> validateKtaApplication(ApplicationKtaInviteResource inviteResource, String errorField) {
        List<ApplicationKtaInvite> existing = applicationKtaInviteRepository.findByApplicationId(inviteResource.getApplication());
        if (!existing.isEmpty()) {
            return serviceFailure(fieldError(format(errorField), inviteResource.getEmail(), "kta.already.invited"));
        }
        ServiceResult<UserResource> userResult = userService.findByEmail(inviteResource.getEmail());
        if (userResult.isFailure() || !userResult.getSuccess().hasRole(Role.KNOWLEDGE_TRANSFER_ADVISOR)) {
            return serviceFailure(fieldError(format(errorField), inviteResource.getEmail(), "user.not.registered.kta"));
        }
        return serviceSuccess();
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
