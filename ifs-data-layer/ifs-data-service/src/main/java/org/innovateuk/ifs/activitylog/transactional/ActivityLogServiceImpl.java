package org.innovateuk.ifs.activitylog.transactional;

import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.domain.ActivityType;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public void recordActivity(long applicationId, ActivityType activityType) {
        applicationRepository.findById(applicationId)
                .ifPresent(application -> {
                    ActivityLog log = new ActivityLog(application, activityType);
                    activityLogRepository.save(log);
                });
    }

    @Override
    public void recordActivityByProjectId(long id, ActivityType type) {

    }

}
