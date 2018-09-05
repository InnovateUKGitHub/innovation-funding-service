package org.innovateuk.ifs.eugrant.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.innovateuk.ifs.eugrant.mapper.EuGrantMapper;
import org.innovateuk.ifs.eugrant.repository.EuGrantRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.EU_GRANT_NOT_READY_TO_SUBMIT;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_FORBIDDEN;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class EuGrantServiceImpl implements EuGrantService {

    @Autowired
    private EuGrantMapper euGrantMapper;

    @Autowired
    private EuGrantRepository euGrantRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Override
    public ServiceResult<Void> save(UUID id, EuGrantResource euGrantResource) {
        return find(euGrantRepository.findOne(id), notFoundError(EuGrant.class, id))
                .andOnSuccess(this::onlyAllowInProgress)
                .andOnSuccessReturn(db ->
                        euGrantRepository.save(
                                euGrantMapper.mapToDomain(euGrantResource)))
                .andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<EuGrantResource> findById(UUID id) {
        return find(euGrantRepository.findOne(id), notFoundError(EuGrant.class, id))
                .andOnSuccess(this::onlyAllowInProgress)
                .andOnSuccessReturn(euGrantMapper::mapToResource);
    }

    @Override
    public ServiceResult<EuGrantResource> create() {
        return serviceSuccess(euGrantMapper.mapToResource(euGrantRepository.save(new EuGrant())));
    }

    @Override
    @Transactional
    public ServiceResult<Void> submit(UUID id) {
        return find(euGrantRepository.findOne(id), notFoundError(EuGrant.class, id))
                .andOnSuccess(this::onlyAllowInProgress)
                .andOnSuccess(this::submit);
    }

    private ServiceResult<EuGrant> onlyAllowInProgress(EuGrant euGrant) {
        if (euGrant.isSubmitted()) {
            return serviceFailure(GENERAL_FORBIDDEN);
        } else {
            return serviceSuccess(euGrant);
        }
    }

    private ServiceResult<Void> submit(EuGrant euGrant) {
        return isReadyToSubmit(euGrant)
                .andOnSuccessReturn(EuGrant::submit)
                .andOnSuccessReturnVoid(this::sendEmail);
    }

    private ServiceResult<EuGrant> sendEmail(EuGrant euGrant) {
        NotificationTarget recipient = new UserNotificationTarget(
                euGrant.getContact().getName(),
                euGrant.getContact().getEmail()
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("euGrant", euGrant);
        notificationArguments.put("projectStartDate", euGrant.getFunding().getProjectStartDate().format(formatter));
        notificationArguments.put("projectEndDate", euGrant.getFunding().getProjectEndDate().format(formatter));

        Notification notification = new Notification(
                systemNotificationSource,
                singletonList(recipient),
                Notifications.GRANT_SUBMITTED,
                notificationArguments
        );
        return notificationService.sendNotificationWithFlush(notification, EMAIL)
                .andOnSuccessReturn(() -> euGrant);
    }

    private ServiceResult<EuGrant> isReadyToSubmit(EuGrant euGrant) {
        if (euGrant.isOrganisationComplete() && euGrant.isContactComplete() && euGrant.isFundingComplete()) {
            return serviceSuccess(euGrant);
        } else {
            return serviceFailure(EU_GRANT_NOT_READY_TO_SUBMIT);
        }
    }

    enum Notifications {
        GRANT_SUBMITTED
    }
}
