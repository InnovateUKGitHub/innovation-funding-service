package com.worth.ifs.application.mapper;

import java.util.function.Function;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;

@Mapper(
    config = GlobalMapperConfig.class
)
public abstract class ApplicationSummaryPageMapper extends PageResourceMapper<Application, ApplicationSummaryResource> {

	@Autowired
	private ApplicationSummaryMapper applicationSummaryMapper;
	
	public ApplicationSummaryPageResource mapToResource(Page<Application> source){
		ApplicationSummaryPageResource result = new ApplicationSummaryPageResource();
		return mapFields(source, result);
	}

	@Override
	protected Function<Application, ApplicationSummaryResource> contentElementConverter() {
		return applicationSummaryMapper::mapToResource;
	}

}
