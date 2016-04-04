package com.worth.ifs.application.mapper;

import java.util.function.Function;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;

@Mapper(
	    config = GlobalMapperConfig.class
	)
public abstract class ClosedCompetitionApplicationSummaryPageMapper extends PageResourceMapper<Application, ClosedCompetitionApplicationSummaryResource>{

	@Autowired
	private ClosedCompetitionApplicationSummaryMapper closedCompetitionApplicationSummaryMapper;
	
	public ClosedCompetitionApplicationSummaryPageResource mapToResource(Page<Application> source){
		ClosedCompetitionApplicationSummaryPageResource result = new ClosedCompetitionApplicationSummaryPageResource();
		return mapFields(source, result);
	}

	@Override
	protected Function<Application, ClosedCompetitionApplicationSummaryResource> contentElementConverter() {
		return closedCompetitionApplicationSummaryMapper::mapToResource;
	}
}
