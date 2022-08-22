package org.innovateuk.ifs.fundingdecision.mapper;

import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.fundingdecision.domain.DecisionStatus;
import org.mapstruct.Mapper;

@Mapper(
	    config = GlobalMapperConfig.class
	)
public abstract class DecisionMapper {

	public Decision mapToResource(DecisionStatus source){
		return Decision.valueOf(source.name());
	}
	
	public DecisionStatus mapToDomain(Decision source){
		return DecisionStatus.valueOf(source.name());
	}
}
