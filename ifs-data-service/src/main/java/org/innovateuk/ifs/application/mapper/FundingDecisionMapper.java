package org.innovateuk.ifs.application.mapper;

import org.mapstruct.Mapper;

import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;

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
