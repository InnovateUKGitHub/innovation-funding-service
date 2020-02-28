package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationHiddenFromDashboard;
import org.innovateuk.ifs.application.domain.DeletedApplicationAudit;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.resource.ApplicationUserCompositeId;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterServiceImpl;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service focused around the processing of Applications.
 */
@Service
public class ApplicationDeletionServiceImpl extends RootTransactionalService implements ApplicationDeletionService {

    public enum Notifications {
        APPLICATION_DELETED
    }

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private ProcessHistoryRepository processHistoryRepository;

    @Autowired
    private DeletedApplicationRepository deletedApplicationRepository;

    @Autowired
    private ApplicationHiddenFromDashboardRepository applicationHiddenFromDashboardRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Override
    @Transactional
    public ServiceResult<Void> deleteApplication(long applicationId) {
        Application application = getApplication(applicationId).getSuccess();
        List<ProcessRole> processRoles = processRoleRepository.findByApplicationId(application.getId());

        return deleteApplicationData(application).andOnSuccess(() -> {
            deletedApplicationRepository.save(new DeletedApplicationAudit(applicationId));
            return sendNotification(application, processRoles);
        });
    }

    private ServiceResult<Void> deleteApplicationData(Application application) {
        applicationFinanceRepository.deleteByApplicationId(application.getId());
        processRoleRepository.deleteByApplicationId(application.getId());
        formInputResponseRepository.deleteByApplicationId(application.getId());
        questionStatusRepository.deleteByApplicationId(application.getId());
        applicationHiddenFromDashboardRepository.deleteByApplicationId(application.getId());
        processHistoryRepository.deleteByProcessId(application.getApplicationProcess().getId());
        applicationRepository.delete(application);
        return serviceSuccess();
    }

    private ServiceResult<Void> sendNotification(Application application, List<ProcessRole> processRoles) {

        List<NotificationTarget> notificationTargets = processRoles.stream()
                .map(ProcessRole::getUser)
                .map(applicant -> new UserNotificationTarget(applicant.getName(), applicant.getEmail()))
                .collect(Collectors.toList());

        User leadApplicant = processRoles.stream()
                .filter(ProcessRole::isLeadApplicant)
                .map(ProcessRole::getUser)
                .findFirst()
                .get();

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationName", application.getName());
        notificationArguments.put("leadUserName", leadApplicant.getName());
        notificationArguments.put("leadEmail", leadApplicant.getEmail());

        Notification notification = new Notification(systemNotificationSource,
                notificationTargets,
                Notifications.APPLICATION_DELETED,
                notificationArguments);

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    @Override
    @Transactional
    public ServiceResult<Void> hideApplicationFromDashboard(ApplicationUserCompositeId id) {
        return getApplication(id.getApplicationId()).andOnSuccessReturnVoid((application) ->
            getUser(id.getUserId()).andOnSuccessReturnVoid(user ->
                    applicationHiddenFromDashboardRepository.save(new ApplicationHiddenFromDashboard(application, user))));
    }

    private ServiceResult<Application> getApplication(long id) {
        return find(applicationRepository.findById(id), notFoundError(Application.class, id));
    }
}
