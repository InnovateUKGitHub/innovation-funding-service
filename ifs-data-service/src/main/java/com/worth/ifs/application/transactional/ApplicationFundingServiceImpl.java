package com.worth.ifs.application.transactional;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;

@Service
public class ApplicationFundingServiceImpl extends BaseTransactionalService implements ApplicationFundingService {

	@Override
	public ServiceResult<Void> makeFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions) {
		
		List<Application> applicationsForCompetition = applicationRepository.findByCompetitionId(competitionId);
		
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
