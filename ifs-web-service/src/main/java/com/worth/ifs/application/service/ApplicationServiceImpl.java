package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.worth.ifs.application.service.Futures.adapt;
import static com.worth.ifs.application.service.Futures.call;
import static java.util.Map.Entry;
import static java.util.stream.Collectors.toMap;
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

        return applicationRestService.getApplicationById(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<ApplicationResource> getInProgress(Long userId) {
        List<ApplicationResource> applications = applicationRestService.getApplicationsByUserId(userId).getSuccessObjectOrThrowException();
        return applications.stream()
                .filter(a -> (fetchApplicationStatusFromId(a.getApplicationStatus()).getName().equals("created") || fetchApplicationStatusFromId(a.getApplicationStatus()).getName().equals("submitted")))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<ApplicationResource> getFinished(Long userId) {
        List<ApplicationResource> applications = applicationRestService.getApplicationsByUserId(userId).getSuccessObjectOrThrowException();
        return applications.stream()
                .filter(a -> (fetchApplicationStatusFromId(a.getApplicationStatus()).getName().equals("approved") || fetchApplicationStatusFromId(a.getApplicationStatus()).getName().equals("rejected")))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Map<Long, Integer> getProgress(Long userId) {
        List<ApplicationResource> applications = applicationRestService.getApplicationsByUserId(userId).getSuccessObjectOrThrowException();
        return call(applications.stream()
                .filter(a -> fetchApplicationStatusFromId(a.getApplicationStatus()).getName().equals("created"))
                .map(ApplicationResource::getId)
                .collect(toMap(id -> id, id -> applicationRestService.getCompleteQuestionsPercentage(id))))
                .entrySet().stream()
                .collect(toMap(Entry::getKey, entry -> entry.getValue().getSuccessObjectOrThrowException().intValue()));
    }

    @Override
    public ApplicationResource createApplication(Long competitionId, Long userId, String applicationName) {
        ApplicationResource application = applicationRestService.createApplication(competitionId, userId, applicationName).getSuccessObjectOrThrowException();

        return application;
    }

    @Override
    public Boolean isApplicationReadyForSubmit(Long applicationId) {
        return applicationRestService.isApplicationReadyForSubmit(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public void updateStatus(Long applicationId, Long statusId) {
        applicationRestService.updateApplicationStatus(applicationId, statusId);
    }

    @Override
    public Future<Integer> getCompleteQuestionsPercentage(Long applicationId) {
        return adapt(applicationRestService.getCompleteQuestionsPercentage(applicationId), re -> re.getSuccessObject().intValue());
    }

    @Override
    public int getAssignedQuestionsCount(Long applicationId, Long processRoleId){
        return applicationRestService.getAssignedQuestionsCount(applicationId, processRoleId).getSuccessObjectOrThrowException().intValue();
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
        return applicationStatusRestService.getApplicationStatusById(id).getSuccessObjectOrThrowException();
    }

}
