package com.worth.ifs.application.mapper;

import org.mapstruct.Mapper;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.fundingdecisiondata.domain.FundingDecisionStatus;

@Mapper(
	    config = GlobalMapperConfig.class
	)
public abstract class FundingDecisionMapper {

	public FundingDecision mapToResource(FundingDecisionStatus source){
		return FundingDecision.valueOf(source.name());
	}
	
	public FundingDecisionStatus mapToDomain(FundingDecision source){
		return FundingDecisionStatus.valueOf(source.name());
	}
}
