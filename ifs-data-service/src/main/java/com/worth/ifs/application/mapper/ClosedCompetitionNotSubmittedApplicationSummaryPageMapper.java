package com.worth.ifs.application.mapper;

import java.util.function.Function;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;

@Mapper(
	    config = GlobalMapperConfig.class
	)
public abstract class ClosedCompetitionNotSubmittedApplicationSummaryPageMapper extends PageResourceMapper<Application, ClosedCompetitionNotSubmittedApplicationSummaryResource>{

	@Autowired
	private ClosedCompetitionNotSubmittedApplicationSummaryMapper closedCompetitionNotSubmittedApplicationSummaryMapper;
	
	public ClosedCompetitionNotSubmittedApplicationSummaryPageResource mapToResource(Page<Application> source){
		ClosedCompetitionNotSubmittedApplicationSummaryPageResource result = new ClosedCompetitionNotSubmittedApplicationSummaryPageResource();
		return mapFields(source, result);
	}

	@Override
	protected Function<Application, ClosedCompetitionNotSubmittedApplicationSummaryResource> contentElementConverter() {
		return closedCompetitionNotSubmittedApplicationSummaryMapper::mapToResource;
	}
}
