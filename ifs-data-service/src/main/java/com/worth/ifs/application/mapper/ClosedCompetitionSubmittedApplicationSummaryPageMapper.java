package com.worth.ifs.application.mapper;

import java.util.function.Function;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;

@Mapper(
	    config = GlobalMapperConfig.class
	)
public abstract class ClosedCompetitionSubmittedApplicationSummaryPageMapper extends PageResourceMapper<Application, ClosedCompetitionSubmittedApplicationSummaryResource>{

	@Autowired
	private ClosedCompetitionSubmittedApplicationSummaryMapper closedCompetitionSubmittedApplicationSummaryMapper;
	
	public ClosedCompetitionSubmittedApplicationSummaryPageResource mapToResource(Page<Application> source){
		ClosedCompetitionSubmittedApplicationSummaryPageResource result = new ClosedCompetitionSubmittedApplicationSummaryPageResource();
		return mapFields(source, result);
	}

	@Override
	protected Function<Application, ClosedCompetitionSubmittedApplicationSummaryResource> contentElementConverter() {
		return closedCompetitionSubmittedApplicationSummaryMapper::mapToResource;
	}
}
