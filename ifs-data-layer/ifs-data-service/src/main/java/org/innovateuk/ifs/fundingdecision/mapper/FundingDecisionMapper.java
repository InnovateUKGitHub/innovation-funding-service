package org.innovateuk.ifs.fundingdecision.mapper;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.mapstruct.Mapper;

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
