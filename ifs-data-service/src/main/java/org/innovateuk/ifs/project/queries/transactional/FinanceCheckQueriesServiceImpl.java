package org.innovateuk.ifs.project.queries.transactional;


import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.service.MappingThreadService;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_SINGLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.QUERIES_CANNOT_BE_SENT_AS_FINANCE_CONTACT_NOT_SUBMITTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElementOrEmpty;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class FinanceCheckQueriesServiceImpl extends AbstractProjectServiceImpl implements FinanceCheckQueriesService {

    private final ThreadService<QueryResource, PostResource> service;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    public enum Notifications {
        NEW_FINANCE_CHECK_QUERY,
        NEW_FINANCE_CHECK_QUERY_RESPONSE
    }


    @Autowired
    public FinanceCheckQueriesServiceImpl(QueryRepository queryRepository, QueryMapper queryMapper, PostMapper postMapper) {
        service = new MappingThreadService<>(queryRepository, queryMapper, postMapper, ProjectFinance.class);
    }

    @Override
    public ServiceResult<QueryResource> findOne(Long id) {
        return service.findOne(id);
    }

    @Override
    public ServiceResult<List<QueryResource>> findAll(Long classContextId) {
        return service.findAll(classContextId);
    }

    @Override
    @Transactional
    public ServiceResult<Void> addPost(PostResource post, Long threadId) {
        return findOne(threadId).andOnSuccess(query -> {
            ProjectFinance projectFinance = projectFinanceRepository.findOne(query.contextClassPk);
            Optional<ProjectUser> financeContact = getFinanceContact(projectFinance.getProject(), projectFinance.getOrganisation());
            if(financeContact.isPresent()) {
                ServiceResult<Void> result = service.addPost(post, threadId);
                if (result.isSuccess()) {
                    if (post.author.hasRole(PROJECT_FINANCE)) {
                        Project project = projectFinance.getProject();
                        return sendResponseNotification(financeContact.get().getUser(), project);
                    }
                }
                return result;
            } else {
                return serviceFailure(forbiddenError(QUERIES_CANNOT_BE_SENT_AS_FINANCE_CONTACT_NOT_SUBMITTED));
            }
        });
    }

    @Override
    @Transactional
    public ServiceResult<Long> create(QueryResource query) {
        return find(projectFinanceRepository.findOne(query.contextClassPk), notFoundError(ProjectFinance.class, query.contextClassPk)).
                andOnSuccess(projectFinance -> {
                    if (getFinanceContact(projectFinance.getProject(), projectFinance.getOrganisation()).isPresent()) {
                        ServiceResult<Long> result = service.create(query);
                        if (result.isSuccess()) {
                            Project project = projectFinance.getProject();
                            List<ProjectUser> projectUsers = project.getProjectUsersWithRole(ProjectParticipantRole.PROJECT_FINANCE_CONTACT);
                            List<ProjectUser> financeContacts = simpleFilter(projectUsers, pu -> Objects.equals(pu.getOrganisation().getId(), projectFinance.getOrganisation().getId()));

                            Optional<ProjectUser> financeContact = getOnlyElementOrEmpty(financeContacts);
                            if (financeContact.isPresent()) {
                                ServiceResult<Void> notificationResult = sendNewQueryNotification(financeContact.get().getUser(), project.getId());

                                if (!notificationResult.isSuccess()) {
                                    return serviceFailure(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE);
                                }
                            }
                        }
                        return result;
                    } else {
                        return serviceFailure(forbiddenError(QUERIES_CANNOT_BE_SENT_AS_FINANCE_CONTACT_NOT_SUBMITTED));
                    }
                });
    }

    private ServiceResult<Void> sendResponseNotification(User financeContact, Project project) {
        NotificationSource from = systemNotificationSource;
        String fullName = financeContact.getName();

        Application application = project.getApplication();

        NotificationTarget pmTarget = new ExternalUserNotificationTarget(fullName, financeContact.getEmail());

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("dashboardUrl", webBaseUrl + "/project-setup/project/" + project.getId());
        notificationArguments.put("applicationName", application.getName());

        Notification notification = new Notification(from, Collections.singletonList(pmTarget), Notifications.NEW_FINANCE_CHECK_QUERY_RESPONSE, notificationArguments);
        return notificationService.sendNotification(notification, NotificationMedium.EMAIL);

    }

    private ServiceResult<Void> sendNewQueryNotification(User financeContact, Long projectId) {

        NotificationSource from = systemNotificationSource;
        String fullName = financeContact.getName();

        NotificationTarget pmTarget = new ExternalUserNotificationTarget(fullName, financeContact.getEmail());

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("dashboardUrl", webBaseUrl + "/project-setup/project/" + projectId);

        Notification notification = new Notification(from, singletonList(pmTarget), FinanceCheckQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY, notificationArguments);
        return notificationService.sendNotification(notification, EMAIL);
    }
}