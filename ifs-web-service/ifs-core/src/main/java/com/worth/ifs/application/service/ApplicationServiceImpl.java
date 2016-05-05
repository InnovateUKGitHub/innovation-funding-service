package com.worth.ifs.application.service;

import static com.worth.ifs.application.service.Futures.adapt;
import static com.worth.ifs.application.service.Futures.call;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.service.CompetitionsRestService;
/**
 * This class contains methods to retrieve and store {@link ApplicationResource} related data,
 * through the RestService {@link ApplicationRestService}.
 */
// TODO DW - INFUND-1555 - get service calls to return rest responses
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionsRestService competitionsRestService;

    @Override
    public ApplicationResource getById(Long applicationId) {
        if (applicationId == null) {
            return null;
        }

        return applicationRestService.getApplicationById(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<ApplicationResource> getInProgress(Long userId) {
        List<ApplicationResource> applications = applicationRestService.getApplicationsByUserId(userId).getSuccessObjectOrThrowException();
        return applications.stream()
                .filter(this::applicationInProgress)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    @Override
    public List<ApplicationResource> getFinished(Long userId) {
        List<ApplicationResource> applications = applicationRestService.getApplicationsByUserId(userId).getSuccessObjectOrThrowException();
        return applications.stream()
                .filter(this::applicationFinished)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    private boolean applicationInProgress(ApplicationResource a) {
    	return applicationStatusInProgress(a) && competitionOpen(a);
    }
    
    private boolean applicationStatusInProgress(ApplicationResource a) {
    	return a.getApplicationStatus().equals(ApplicationStatusConstants.CREATED.getId())
		        || a.getApplicationStatus().equals(ApplicationStatusConstants.SUBMITTED.getId())
		        || a.getApplicationStatus().equals(ApplicationStatusConstants.OPEN.getId());
    }
    
    private boolean applicationFinished(ApplicationResource a) {
    	return (applicationStatusInProgress(a) && competitionClosed(a)) || applicationStatusFinished(a);
    }
    
    private boolean applicationStatusFinished(ApplicationResource a) {
    	return a.getApplicationStatus().equals(ApplicationStatusConstants.APPROVED.getId())
                || a.getApplicationStatus().equals(ApplicationStatusConstants.REJECTED.getId());
    }
    
    private boolean competitionOpen(ApplicationResource a) {
    	CompetitionResource competition = competitionsRestService.getCompetitionById(a.getCompetition()).getSuccessObjectOrThrowException();
    	return CompetitionResource.Status.OPEN.equals(competition.getCompetitionStatus());
    }
    
    private boolean competitionClosed(ApplicationResource a) {
    	return !competitionOpen(a);
    }
    
    @Override
    public Map<Long, Integer> getProgress(Long userId) {
        List<ApplicationResource> applications = applicationRestService.getApplicationsByUserId(userId).getSuccessObjectOrThrowException();
        return call(applications.stream()
                .filter(this::applicationInProgress)
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

}
