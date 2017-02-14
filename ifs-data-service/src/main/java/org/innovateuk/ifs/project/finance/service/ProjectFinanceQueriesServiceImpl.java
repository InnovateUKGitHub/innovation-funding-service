package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.service.MappingThreadService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_SINGLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElementOrEmpty;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectFinanceQueriesServiceImpl extends MappingThreadService<Query, QueryResource, QueryMapper, ProjectFinance>
        implements ProjectFinanceQueriesService {


    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    public enum Notifications {
        NEW_FINANCE_CHECK_QUERY
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    public ProjectFinanceQueriesServiceImpl(QueryRepository queryRepository, QueryMapper queryMapper, PostMapper postMapper) {
        super(queryRepository, queryMapper, postMapper, ProjectFinance.class);
    }

    @Override
    public ServiceResult<Long> create(QueryResource query) {
        ServiceResult<Long> result = super.create(query);
        if (result.isSuccess()) {
            ServiceResult<ProjectFinance> projectFinance = find(projectFinanceRepository.findOne(query.contextClassPk), notFoundError(ProjectFinance.class, query.contextClassPk));
            if (projectFinance.isSuccess()) {
                Long projectId = projectFinance.getSuccessObject().getProject().getId();
                List<ProjectUser> projectUsers = projectFinance.getSuccessObject().getProject().getProjectUsers();

                NotificationSource from = systemNotificationSource;

                List<ProjectUser> financeContacts = simpleFilter(projectUsers, pu -> pu.getRole().isFinanceContact());
                User financeContact = getOnlyElementOrEmpty(financeContacts).get().getUser();

                String fullName = financeContact.getName();

                NotificationTarget pmTarget = new ExternalUserNotificationTarget(fullName, financeContact.getEmail());

                Map<String, Object> notificationArguments = new HashMap<>();
                notificationArguments.put("dashboardUrl", webBaseUrl + "/project-setup/project/" + projectId);

                Notification notification = new Notification(from, singletonList(pmTarget), ProjectFinanceQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY, notificationArguments);
                ServiceResult<Void> notificationResult = notificationService.sendNotification(notification, EMAIL);

                if (!notificationResult.isSuccess()) {
                    return serviceFailure(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE);
                }
            }
        }
        return result;
    }
}