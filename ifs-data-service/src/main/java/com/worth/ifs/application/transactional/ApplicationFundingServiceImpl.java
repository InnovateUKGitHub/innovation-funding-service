package com.worth.ifs.application.transactional;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.transactional.BaseTransactionalService;

@Service
public class ApplicationFundingServiceImpl extends BaseTransactionalService implements ApplicationFundingService {

    private static final Log LOG = LogFactory.getLog(ApplicationFundingServiceImpl.class);

	@Override
	public ServiceResult<Void> makeFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions) {
		
		Competition competition = competitionRepository.findOne(competitionId);
		if(competition == null) {
			LOG.error("cannot make funding decision for an inexistant competition: " + competitionId);
			throw new IllegalArgumentException("invalid competition id");
		}
		
		if(!CompetitionResource.Status.FUNDERS_PANEL.equals(competition.getCompetitionStatus())){
			LOG.error("cannot make funding decision for a competition not in FUNDERS_PANEL status: " + competitionId);
			throw new IllegalArgumentException("competition not in correct status");
		}
		
		List<Application> applicationsForCompetition = applicationRepository.findByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.SUBMITTED.getId());
		
		boolean allPresent = applicationsForCompetition.stream().noneMatch(app -> !applicationFundingDecisions.containsKey(app.getId()));
		
		if(!allPresent) {
			return serviceFailure(CommonErrors.badRequestError("not all applications represented in funding decision"));
		}
		
		applicationsForCompetition.forEach(app -> {
			FundingDecision applicationFundingDecision = applicationFundingDecisions.get(app.getId());
			ApplicationStatus status = statusFromDecision(applicationFundingDecision);
			app.setApplicationStatus(status);
			applicationRepository.save(app);
		});
		
		competition.setFundersPanelEndDate(LocalDateTime.now());
		competitionRepository.save(competition);
		
		return serviceSuccess();
	}
	
	private ApplicationStatus statusFromDecision(FundingDecision applicationFundingDecision) {
		if(FundingDecision.FUNDED.equals(applicationFundingDecision)) {
			return statusFromConstant(ApplicationStatusConstants.APPROVED);
		} else {
			return statusFromConstant(ApplicationStatusConstants.REJECTED);
		}
	}

	private ApplicationStatus statusFromConstant(ApplicationStatusConstants applicationStatusConstant) {
		return applicationStatusRepository.findOne(applicationStatusConstant.getId());
	}
	
}
