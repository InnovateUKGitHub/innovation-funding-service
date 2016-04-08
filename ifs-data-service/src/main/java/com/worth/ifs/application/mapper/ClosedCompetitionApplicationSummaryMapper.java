package com.worth.ifs.application.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.user.domain.ProcessRole;

@Mapper(config = GlobalMapperConfig.class)
public abstract class ClosedCompetitionApplicationSummaryMapper {

	@Autowired
	private ApplicationFinanceRestService applicationFinanceRestService;
	
	public ClosedCompetitionApplicationSummaryResource mapToResource(Application source){
		
		ClosedCompetitionApplicationSummaryResource result = new ClosedCompetitionApplicationSummaryResource();
		
		result.setId(source.getId());
		result.setLead(source.getLeadOrganisation().getName());
		result.setName(source.getName());
		result.setDuration(source.getDurationInMonths());
		
		RestResult<List<ApplicationFinanceResource>> applicationFinancesResult = applicationFinanceRestService.getApplicationFinances(source.getId());
		
		BigDecimal grantRequested = getGrantRequested(applicationFinancesResult);
		result.setGrantRequested(grantRequested);
		
		int numberOfPartners = source.getProcessRoles().stream().collect(Collectors.groupingBy(ProcessRole::getOrganisation)).size();
		result.setNumberOfPartners(numberOfPartners);
		
		BigDecimal totalProjectCost = getTotalProjectCost(applicationFinancesResult);
		result.setTotalProjectCost(totalProjectCost);
		return result;
	}

	private BigDecimal getTotalProjectCost(RestResult<List<ApplicationFinanceResource>> applicationFinancesResult) {
		if(applicationFinancesResult.isFailure()){
			return BigDecimal.ZERO;
		}
		List<ApplicationFinanceResource> result = applicationFinancesResult.getSuccessObject();
		
		return result.stream().map(res -> res.getTotal()).reduce((i, j) -> i.add(j)).get();
	}

	private BigDecimal getGrantRequested(RestResult<List<ApplicationFinanceResource>> applicationFinancesResult) {
		if(applicationFinancesResult.isFailure()){
			return BigDecimal.ZERO;
		}
		List<ApplicationFinanceResource> result = applicationFinancesResult.getSuccessObject();
		
		return result.stream().map(res -> {
			if(res.getGrantClaim() != null){
				return res.getGrantClaim().getTotal();
			}
			return BigDecimal.ZERO;
		}).reduce((i, j) -> {
			return i.add(j);
		}).get();
	}
}
