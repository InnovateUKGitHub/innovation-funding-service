package org.innovateuk.ifs.activitylog.transactional;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.hibernate.Hibernate.initialize;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Slf4j
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
        Optional<Application> application = applicationRepository.findById(applicationId);
        if(application.isPresent()) {
            ActivityLog log = new ActivityLog(application.get(), activityType);
            activityLogRepository.save(log);
        } else {
            log.error(format("application %d not found", applicationId));
        }
    }

    @Override
    public void recordActivityByApplicationId(long applicationId, long authorId, ActivityType activityType) {
        Optional<Application> application = applicationRepository.findById(applicationId);
        if(application.isPresent()) {
            Optional<User> user = userRepository.findById(authorId);
            if(user.isPresent()) {
                ActivityLog log = new ActivityLog(application.get(), activityType, null, user.get());
                activityLogRepository.save(log);
            } else {
                log.error(format("author %d not found", authorId));
            }
        } else {
            log.error(format("application %d not found", applicationId));
        }
    }

    @Override
    public void recordActivityByProjectId(long projectId, ActivityType activityType) {
        Optional<Project> project = projectRepository.findById(projectId);
        if(project.isPresent()) {
            ActivityLog log = new ActivityLog(project.get().getApplication(), activityType);
            activityLogRepository.save(log);
        } else {
            log.error(format("project %d not found", projectId));
        }
    }

    @Override
    public void recordActivityByProjectIdAndOrganisationIdAndAuthorId(long projectId, long organisationId, long authorId, ActivityType activityType) {
        Optional<Project> project = projectRepository.findById(projectId);
        if(project.isPresent()) {
            Optional<Organisation> organisation = organisationRepository.findById(organisationId);
            if(organisation.isPresent()) {
                Optional<User> user = userRepository.findById(authorId);
                if(user.isPresent()) {
                    ActivityLog log = new ActivityLog(project.get().getApplication(), activityType, organisation.get(), user.get());
                    activityLogRepository.save(log);
                } else {
                    log.error(format("authorId %d not found", authorId));
                }
            } else {
                log.error(format("organisation %d not found", organisationId));
            }
        } else {
            log.error(format("project %d not found", projectId));
        }
    }

    @Override
    public void recordActivityByProjectIdAndOrganisationId(long projectId, long organisationId, ActivityType activityType) {
        Optional<Project> project = projectRepository.findById(projectId);
        if(project.isPresent()) {
            Optional<Organisation> organisation = organisationRepository.findById(organisationId);
            if(organisation.isPresent()) {
                ActivityLog log = new ActivityLog(project.get().getApplication(), activityType, organisation.get());
                activityLogRepository.save(log);
            } else {
                log.error(format("organisation %d not found", organisationId));
            }
        } else {
            log.error(format("project %d not found", projectId));
        }
    }

    @Override
    public void recordDocumentActivityByProjectId(long projectId, ActivityType activityType, long documentConfigId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if(project.isPresent()) {
            Optional<CompetitionDocument> document = competitionDocumentConfigRepository.findById(documentConfigId);
            if(document.isPresent()) {
                ActivityLog log = new ActivityLog(project.get().getApplication(), activityType, document.get());
                activityLogRepository.save(log);
            } else {
                log.error(format("document %d not found", documentConfigId));
            }
        } else {
            log.error(format("project %d not found", projectId));
        }
    }

    @Override
    public void recordQueryActivityByProjectFinanceId(long projectFinanceId, ActivityType activityType, long threadId) {
        Optional<ProjectFinance> projectFinance = projectFinanceRepository.findById(projectFinanceId);
        if(projectFinance.isPresent()) {
            Optional<Query> query = queryRepository.findById(threadId);
            if(query.isPresent()) {
                ActivityLog log = new ActivityLog(projectFinance.get().getProject().getApplication(), activityType, query.get(), projectFinance.get().getOrganisation());
                activityLogRepository.save(log);
            } else {
                log.error(format("query %d not found", threadId));
            }
        } else {
            log.error(format("project finance %d not found", projectFinanceId));
        }
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
