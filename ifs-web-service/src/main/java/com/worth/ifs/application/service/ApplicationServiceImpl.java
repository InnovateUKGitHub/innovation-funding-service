package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link ApplicationResource} related data,
 * through the RestService {@link ApplicationRestService}.
 */
// TODO DW - INFUND-1555 - get service calls to return rest responses
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    ApplicationRestService applicationRestService;

    @Autowired
    ApplicationStatusRestService applicationStatusRestService;

    @Override
    public ApplicationResource getById(Long applicationId, Boolean... hateoas) {
        if (applicationId == null) {
            return null;
        }

        return applicationRestService.getApplicationById(applicationId).handleSuccessOrFailure(
            failure -> null,
            success -> success
        );
    }

    @Override
    public List<ApplicationResource> getInProgress(Long userId) {
        List<ApplicationResource> applications = applicationRestService.getApplicationsByUserId(userId).getSuccessObject();
        return applications.stream()
                .filter(a -> (fetchApplicationStatusFromId(a.getApplicationStatus()).getName().equals("created") || fetchApplicationStatusFromId(a.getApplicationStatus()).getName().equals("submitted")))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<ApplicationResource> getFinished(Long userId) {
        List<ApplicationResource> applications = applicationRestService.getApplicationsByUserId(userId).getSuccessObject();
        return applications.stream()
                .filter(a -> (fetchApplicationStatusFromId(a.getApplicationStatus()).getName().equals("approved") || fetchApplicationStatusFromId(a.getApplicationStatus()).getName().equals("rejected")))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Map<Long, Integer> getProgress(Long userId) {
        List<ApplicationResource> applications = applicationRestService.getApplicationsByUserId(userId).getSuccessObject();
        Map<Long, Integer> applicationProgress = new HashMap<>();
        applications.stream()
            .filter(a -> fetchApplicationStatusFromId(a.getApplicationStatus()).getName().equals("created"))
            .map(ApplicationResource::getId)
            .forEach(id -> {
                Double progress = applicationRestService.getCompleteQuestionsPercentage(id).getSuccessObject();
                applicationProgress.put(id, progress.intValue());
            });
        return applicationProgress;
    }

    @Override
    public ApplicationResource createApplication(Long competitionId, Long userId, String applicationName) {
        ApplicationResource application = applicationRestService.createApplication(competitionId, userId, applicationName).getSuccessObject();

        return application;
    }

    @Override
    public Boolean isApplicationReadyForSubmit(Long applicationId) {
        return applicationRestService.isApplicationReadyForSubmit(applicationId).getSuccessObject();
    }

    @Override
    public void updateStatus(Long applicationId, Long statusId) {
        applicationRestService.updateApplicationStatus(applicationId, statusId);
    }

    @Override
    public int getCompleteQuestionsPercentage(Long applicationId) {
       return applicationRestService.getCompleteQuestionsPercentage(applicationId).getSuccessObject().intValue();
    }

    @Override
    public int getAssignedQuestionsCount(Long applicationId, Long processRoleId){
        return applicationRestService.getAssignedQuestionsCount(applicationId, processRoleId).getSuccessObject().intValue();
    }

    @Override
    public void save(ApplicationResource application) {
        applicationRestService.saveApplication(application);
    }

    @Override
    public RestResult<ApplicationResource> findByProcessRoleId(Long id) {
        return applicationRestService.findByProcessRoleId(id);
    }

    private ApplicationStatusResource fetchApplicationStatusFromId(Long id){
        return applicationStatusRestService.getApplicationStatusById(id).getSuccessObject();
    }

}
