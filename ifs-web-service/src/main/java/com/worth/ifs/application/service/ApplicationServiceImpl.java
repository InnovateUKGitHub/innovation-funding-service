package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link Application} related data,
 * through the RestService {@link ApplicationRestService}.
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    ApplicationRestService applicationRestService;

    @Override
    public Application getById(Long applicationId) {
        return applicationRestService.getApplicationById(applicationId);
    }

    @Override
    public List<Application> getInProgress(Long userId) {
        List<Application> applications = applicationRestService.getApplicationsByUserId(userId);
        return applications.stream()
                .filter(a -> (a.getApplicationStatus().getName().equals("created") || a.getApplicationStatus().getName().equals("submitted")))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Application> getFinished(Long userId) {
        List<Application> applications = applicationRestService.getApplicationsByUserId(userId);
        return applications.stream()
                .filter(a -> (a.getApplicationStatus().getName().equals("approved") || a.getApplicationStatus().getName().equals("rejected")))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Map<Long, Integer> getProgress(Long userId) {
        List<Application> applications = applicationRestService.getApplicationsByUserId(userId);
        Map<Long, Integer> applicationProgress = new HashMap<>();
        for (Application application : applications) {
            if(application.getApplicationStatus().getName().equals("created")){
                Double progress = applicationRestService.getCompleteQuestionsPercentage(application.getId());
                applicationProgress.put(application.getId(), progress.intValue());
            }
        }
        return applicationProgress;
    }

    @Override
    public Application createApplication(Long competitionId, Long organisationId, Long userId, String applicationName) {
        Application application = applicationRestService.createApplication(competitionId, organisationId, userId, applicationName);

        return application;
    }

    @Override
    public void updateStatus(Long applicationId, Long statusId) {
        applicationRestService.updateApplicationStatus(applicationId, statusId);
    }

    @Override
    public int getCompleteQuestionsPercentage(Long applicationId) {
       return applicationRestService.getCompleteQuestionsPercentage(applicationId).intValue();
    }

    @Override
    public void save(Application application) {
        applicationRestService.saveApplication(application);
    }
}
