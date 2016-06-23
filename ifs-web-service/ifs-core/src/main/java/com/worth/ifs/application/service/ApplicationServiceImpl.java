package com.worth.ifs.application.service;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.service.CompetitionsRestService;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.worth.ifs.application.service.Futures.adapt;
import static com.worth.ifs.application.service.Futures.call;
import static java.util.stream.Collectors.toMap;
/**
 * This class contains methods to retrieve and store {@link ApplicationResource} related data,
 * through the RestService {@link ApplicationRestService}.
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionsRestService competitionsRestService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationService organisationService;

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
    	boolean applicationInProgressForOpenCompetition = applicationStatusInProgress(a) && competitionOpen(a);
    	boolean applicationSubmittedForCompetitionNotYetFinishedFunding = applicationStatusSubmitted(a) && competitionFundingNotYetComplete(a);

    	return applicationInProgressForOpenCompetition || applicationSubmittedForCompetitionNotYetFinishedFunding;
    }
   
    private boolean applicationFinished(ApplicationResource a) {
    	boolean applicationInProgressForClosedCompetition = applicationStatusInProgress(a) && competitionClosed(a);
    	boolean applicationSubmittedForCompetitionFinishedFunding = applicationStatusSubmitted(a) && competitionFundingComplete(a);
    	boolean applicationFinished = applicationStatusFinished(a);
    	
    	return applicationInProgressForClosedCompetition || applicationSubmittedForCompetitionFinishedFunding || applicationFinished;
    }
    
    private boolean applicationStatusInProgress(ApplicationResource a) {
    	return appStatusIn(a, ApplicationStatusConstants.CREATED, ApplicationStatusConstants.OPEN);
    }

	private boolean applicationStatusFinished(ApplicationResource a) {
		return appStatusIn(a, ApplicationStatusConstants.APPROVED, ApplicationStatusConstants.REJECTED);
    }
	
    private boolean applicationStatusSubmitted(ApplicationResource a) {
    	return a.getApplicationStatus().equals(ApplicationStatusConstants.SUBMITTED.getId());
    }
    
    private boolean competitionOpen(ApplicationResource a) {
    	CompetitionResource competition = competitionsRestService.getCompetitionById(a.getCompetition()).getSuccessObjectOrThrowException();
    	return CompetitionResource.Status.OPEN.equals(competition.getCompetitionStatus());
    }
    
    private boolean competitionFundingNotYetComplete(ApplicationResource a) {
    	CompetitionResource competition = competitionsRestService.getCompetitionById(a.getCompetition()).getSuccessObjectOrThrowException();
    	return compStatusIn(competition, CompetitionResource.Status.OPEN, CompetitionResource.Status.IN_ASSESSMENT, CompetitionResource.Status.FUNDERS_PANEL);
    }
    
    private boolean competitionFundingComplete(ApplicationResource a) {
    	CompetitionResource competition = competitionsRestService.getCompetitionById(a.getCompetition()).getSuccessObjectOrThrowException();
    	return compStatusIn(competition, CompetitionResource.Status.ASSESSOR_FEEDBACK, CompetitionResource.Status.PROJECT_SETUP);
	}
    
    private boolean appStatusIn(ApplicationResource app, ApplicationStatusConstants... statuses) {
    	for(ApplicationStatusConstants status: statuses) {
    		if(status.getId().equals(app.getApplicationStatus())) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean compStatusIn(CompetitionResource comp, CompetitionResource.Status... statuses) {
    	for(CompetitionResource.Status status: statuses) {
    		if(status.equals(comp.getCompetitionStatus())) {
    			return true;
    		}
    	}
    	return false;
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

    @Override
    public OrganisationResource getLeadOrganisation(Long applicationId) {
        ApplicationResource application = getById(applicationId);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        return organisationService.getOrganisationById(leadApplicantProcessRole.getOrganisation());
    }



}
