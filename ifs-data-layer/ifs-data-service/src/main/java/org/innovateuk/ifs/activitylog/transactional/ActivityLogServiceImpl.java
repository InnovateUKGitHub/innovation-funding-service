package org.innovateuk.ifs.activitylog.transactional;

import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hibernate.Hibernate.initialize;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private CompetitionDocumentConfigRepository competitionDocumentConfigRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    public void recordActivityByApplicationId(long applicationId, ActivityType activityType) {
        applicationRepository.findById(applicationId)
                .ifPresent(application -> {
                    ActivityLog log = new ActivityLog(application, activityType);
                    activityLogRepository.save(log);
                });
    }

    @Override
    public void recordActivityByProjectId(long projectId, ActivityType activityType) {
        projectRepository.findById(projectId)
                .ifPresent(project -> {
                    ActivityLog log = new ActivityLog(project.getApplication(), activityType);
                    activityLogRepository.save(log);
                });
    }

    @Override
    public void recordActivityByProjectIdAndOrganisationIdAndAuthorId(long projectId, long organisationId, long authorId, ActivityType activityType) {
        projectRepository.findById(projectId)
                .ifPresent(project -> {
                    organisationRepository.findById(organisationId)
                            .ifPresent(organisation -> {
                                userRepository.findById(authorId).ifPresent(user -> {
                                    ActivityLog log = new ActivityLog(project.getApplication(), activityType, organisation, user);
                                    activityLogRepository.save(log);
                                });
                            });
                });
    }

    @Override
    public void recordActivityByProjectIdAndOrganisationId(long projectId, long organisationId, ActivityType activityType) {
        projectRepository.findById(projectId)
                .ifPresent(project -> {
                    organisationRepository.findById(organisationId).ifPresent(organisation -> {
                        ActivityLog log = new ActivityLog(project.getApplication(), activityType, organisation);
                        activityLogRepository.save(log);
                    });
                });
    }

    @Override
    public void recordDocumentActivityByProjectId(long projectId, ActivityType activityType, long documentConfigId) {
        projectRepository.findById(projectId)
                .ifPresent(project -> {
                    competitionDocumentConfigRepository.findById(documentConfigId).ifPresent(document -> {
                        ActivityLog log = new ActivityLog(project.getApplication(), activityType, document);
                        activityLogRepository.save(log);
                    });
                });
    }

    @Override
    public void recordQueryActivityByProjectFinanceId(long projectFinanceId, ActivityType activityType, long threadId) {
        projectFinanceRepository.findById(projectFinanceId)
                .ifPresent(projectFinance -> {
                    queryRepository.findById(threadId).ifPresent(query -> {
                        ActivityLog log = new ActivityLog(projectFinance.getProject().getApplication(), activityType, query, projectFinance.getOrganisation());
                        activityLogRepository.save(log);
                    });
                });
    }

    @Transactional(readOnly = true)
    public ServiceResult<List<ActivityLogResource>> findByApplicationId(long applicationId) {
        return serviceSuccess(activityLogRepository.findByApplicationIdOrderByCreatedOnDesc(applicationId)
                .stream()
                .map(ActivityLogServiceImpl::toResource)
                .collect(toList()));
    }

    private static ActivityLogResource toResource(ActivityLog activityLog) {
        initialize(activityLog.getAuthor().getRoles());
        return new ActivityLogResource(
                activityLog.getType(),
                activityLog.getAuthor().getId(),
                activityLog.getAuthor().getName(),
                activityLog.getAuthor().getRoles(),
                activityLog.getCreatedOn(),
                activityLog.getOrganisation().map(Organisation::getId).orElse(null),
                activityLog.getOrganisation().map(Organisation::getName).orElse(null),
                activityLog.getCompetitionDocument().map(CompetitionDocument::getId).orElse(null),
                activityLog.getCompetitionDocument().map(CompetitionDocument::getTitle).orElse(null),
                activityLog.getQuery().map(Query::id).orElse(null),
                activityLog.getQuery().map(Query::section).orElse(null),
                activityLog.isOrganisationRemoved()
        );
    }
}
