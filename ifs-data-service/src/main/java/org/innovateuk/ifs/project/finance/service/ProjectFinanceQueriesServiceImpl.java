package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.transactional.FinanceCheckServiceImpl;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.service.MappingThreadService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_SINGLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElementOrEmpty;
import static org.innovateuk.ifs.util.CollectionFunctions.pairsToMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Service
public class ProjectFinanceQueriesServiceImpl extends MappingThreadService<Query, QueryResource, QueryMapper, ProjectFinance>
        implements ProjectFinanceQueriesService {

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        NEW_FINANCE_CHECK_QUERY_RESPONSE
    }

    @Autowired
    public ProjectFinanceQueriesServiceImpl(QueryRepository queryRepository, QueryMapper queryMapper, PostMapper postMapper) {
        super(queryRepository, queryMapper, postMapper, ProjectFinance.class);
    }

    @Override
    public ServiceResult<Void> addPost(PostResource post, Long threadId) {
        ServiceResult<Void> result = super.addPost(post, threadId);
        if (result.isSuccess()) {
            if (post.author.hasRole(UserRoleType.PROJECT_FINANCE)) {
                ServiceResult<QueryResource> query = super.findOne(threadId);
                if (query.isSuccess()) {
                    ProjectFinance projectFinance = projectFinanceRepository.findOne(query.getSuccessObject().contextClassPk);
                    Project project = projectFinance.getProject();

                    List<ProjectUser> projectUsers = project.getProjectUsers();
                    List<ProjectUser> financeContacts = simpleFilter(projectUsers, pu -> pu.getRole().isFinanceContact());
                    Optional<ProjectUser> financeContact = getOnlyElementOrEmpty(financeContacts);

                    if (financeContact.isPresent()) {
                        NotificationSource from = systemNotificationSource;
                        String fullName = financeContact.get().getUser().getName();

                        Application application = project.getApplication();

                        NotificationTarget pmTarget = new ExternalUserNotificationTarget(fullName, financeContact.get().getUser().getEmail());

                        Map<String, Object> notificationArguments = new HashMap<>();
                        notificationArguments.put("dashboardUrl", webBaseUrl + "/project-setup/project/" + project.getId());
                        notificationArguments.put("applicationName", application.getName());

                        Notification notification = new Notification(from, Collections.singletonList(pmTarget), ProjectFinanceQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY_RESPONSE, notificationArguments);
                        ServiceResult<Void> notificationResult = notificationService.sendNotification(notification, NotificationMedium.EMAIL);

                        if (!notificationResult.isSuccess()) {
                            return serviceFailure(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE);
                        }
                    }
                }
            }
        }
        return result;
    }
}