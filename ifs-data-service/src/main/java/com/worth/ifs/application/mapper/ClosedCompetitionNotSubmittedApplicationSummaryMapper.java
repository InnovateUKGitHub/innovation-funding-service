package com.worth.ifs.application.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.commons.service.ServiceResult;

@Mapper(config = GlobalMapperConfig.class)
public abstract class ClosedCompetitionNotSubmittedApplicationSummaryMapper {

	@Autowired
	private ApplicationService applicationService;
	
	public ClosedCompetitionNotSubmittedApplicationSummaryResource mapToResource(Application source){
		
		ClosedCompetitionNotSubmittedApplicationSummaryResource result = new ClosedCompetitionNotSubmittedApplicationSummaryResource();
		
		result.setId(source.getId());
		result.setLead(source.getLeadOrganisation().getName());
		result.setName(source.getName());
		
		ServiceResult<CompletedPercentageResource> percentageResult = applicationService.getProgressPercentageByApplicationId(source.getId());
		if(percentageResult.isSuccess()){
			result.setCompletedPercentage(percentageResult.getSuccessObject().getCompletedPercentage().intValue());
		}
		
		return result;
	}

}
