package org.innovateuk.ifs.activitylog.transactional;

import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.domain.ActivityType;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private CompetitionDocumentConfigRepository competitionDocumentConfigRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public void recordActivityByApplicationId(long applicationId, ActivityType activityType) {
        applicationRepository.findById(applicationId)
                .ifPresent(application -> {
                    ActivityLog log = new ActivityLog(application, activityType);
                    activityLogRepository.save(log);
                });
    }

    @Override
    public void recordActivityByProjectId(long applicationId, ActivityType activityType) {

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
                        ActivityLog log = new ActivityLog(projectFinance.getProject().getApplication(), activityType, query);
                        activityLogRepository.save(log);
                    });
                });
    }

}
