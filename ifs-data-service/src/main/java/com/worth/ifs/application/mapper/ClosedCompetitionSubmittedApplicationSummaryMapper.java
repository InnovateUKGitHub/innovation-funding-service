package com.worth.ifs.application.mapper;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryResource;
import com.worth.ifs.application.transactional.ApplicationSummarisationService;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.ProcessRole;

@Mapper(config = GlobalMapperConfig.class)
public abstract class ClosedCompetitionSubmittedApplicationSummaryMapper {

	@Autowired
	private ApplicationSummarisationService applicationSummarisationService;
	
	public ClosedCompetitionSubmittedApplicationSummaryResource mapToResource(Application source){
		
		ClosedCompetitionSubmittedApplicationSummaryResource result = new ClosedCompetitionSubmittedApplicationSummaryResource();
		
		result.setId(source.getId());
		result.setLead(source.getLeadOrganisation().getName());
		result.setName(source.getName());
		result.setDuration(source.getDurationInMonths());
		
		
		BigDecimal grantRequested = getGrantRequested(source);
		result.setGrantRequested(grantRequested);
		
		int numberOfPartners = source.getProcessRoles().stream().collect(Collectors.groupingBy(ProcessRole::getOrganisation)).size();
		result.setNumberOfPartners(numberOfPartners);
		
		BigDecimal totalProjectCost = getTotalProjectCost(source);
		result.setTotalProjectCost(totalProjectCost);
		return result;
	}

	private BigDecimal getTotalProjectCost(Application source) {
		ServiceResult<BigDecimal> totalCostResult = applicationSummarisationService.getTotalProjectCost(source);
		if(totalCostResult.isFailure()){
			return BigDecimal.ZERO;
		}
		return totalCostResult.getSuccessObject();
	}

	private BigDecimal getGrantRequested(Application source) {
		ServiceResult<BigDecimal> fundingSoughtResult = applicationSummarisationService.getFundingSought(source);
		if(fundingSoughtResult.isFailure()){
			return BigDecimal.ZERO;
		}
		return fundingSoughtResult.getSuccessObject();
	}
}
