package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.commons.service.ServiceResult;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Mapper(config = GlobalMapperConfig.class)
public abstract class ApplicationSummaryMapper {

	@Autowired
	private ApplicationService applicationService;
	
	public ApplicationSummaryResource mapToResource(Application source){
		
		ApplicationSummaryResource result = new ApplicationSummaryResource();
		
		ServiceResult<CompletedPercentageResource> percentageResult = applicationService.getProgressPercentageByApplicationId(source.getId());
		if(percentageResult.isSuccess()){
			result.setCompletedPercentage(percentageResult.getSuccessObject().getCompletedPercentage().intValue());
		}
		
		result.setApplicationStatus(source.getApplicationStatus().getId());
		result.setApplicationStatusName(source.getApplicationStatus().getName());
		result.setId(source.getId());
		result.setLead(source.getLeadApplicant().getName());
		result.setName(source.getName());
		return result;
	}

	public Iterable<ApplicationSummaryResource> mapToResource(Iterable<Application> source){
		ArrayList<ApplicationSummaryResource> result = new ArrayList<>();
		for (Application application : source) {
			result.add(mapToResource(application));
		}
		return result;
	}
    
}
