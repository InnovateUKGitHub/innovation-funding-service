package org.innovateuk.ifs.application.mapper;

import java.util.function.Function;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;

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
