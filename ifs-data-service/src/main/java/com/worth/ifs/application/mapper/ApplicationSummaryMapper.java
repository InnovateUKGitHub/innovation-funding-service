package com.worth.ifs.application.mapper;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.application.transactional.ApplicationSummarisationService;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.ProcessRole;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Mapper(config = GlobalMapperConfig.class)
public abstract class ApplicationSummaryMapper {

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ApplicationSummarisationService applicationSummarisationService;

	@Autowired
	private FundingDecisionMapper fundingDecisionMapper;

	public ApplicationSummaryResource mapToResource(Application source){

		ApplicationSummaryResource result = new ApplicationSummaryResource();

		ServiceResult<CompletedPercentageResource> percentageResult = applicationService.getProgressPercentageByApplicationId(source.getId());
		if(percentageResult.isSuccess()){
			result.setCompletedPercentage(percentageResult.getSuccessObject().getCompletedPercentage().intValue());
		}

		result.setStatus(status(source, result.getCompletedPercentage()));
		result.setId(source.getId());
        result.setName(source.getName());
        result.setDuration(source.getDurationInMonths());

        if(source.getLeadApplicant()!=null) {
            result.setLeadApplicant(source.getLeadApplicant().getName());
        }

        if(source.getLeadOrganisation()!=null) {
            result.setLead(source.getLeadOrganisation().getName());
        }

		if(source.getFundingDecision() != null) {
			result.setFundingDecision(fundingDecisionMapper.mapToResource(source.getFundingDecision()));
		}
		if(ApplicationStatusConstants.APPROVED.getId().equals(source.getApplicationStatus().getId())) {
			result.setFundingDecision(FundingDecision.FUNDED);
		}

		BigDecimal grantRequested = getGrantRequested(source);
		result.setGrantRequested(grantRequested);

		int numberOfPartners = source.getProcessRoles().stream().collect(Collectors.groupingBy(ProcessRole::getOrganisation)).size();
		result.setNumberOfPartners(numberOfPartners);

		BigDecimal totalProjectCost = getTotalProjectCost(source);
		result.setTotalProjectCost(totalProjectCost);

		return result;
	}

	private String status(Application source, Integer completedPercentage) {

		if(ApplicationStatusConstants.SUBMITTED.getId().equals(source.getApplicationStatus().getId())
				|| ApplicationStatusConstants.APPROVED.getId().equals(source.getApplicationStatus().getId())
				|| ApplicationStatusConstants.REJECTED.getId().equals(source.getApplicationStatus().getId())) {
			return "Submitted";
		}

		if(completedPercentage != null && completedPercentage > 50) {
			return "In Progress";
		}
		return "Started";
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

	public Iterable<ApplicationSummaryResource> mapToResource(Iterable<Application> source){
		ArrayList<ApplicationSummaryResource> result = new ArrayList<>();
		for (Application application : source) {
			result.add(mapToResource(application));
		}
		return result;
	}

}
