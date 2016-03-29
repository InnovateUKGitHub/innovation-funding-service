package com.worth.ifs.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;

@Mapper(
	    config = GlobalMapperConfig.class,
		uses = {
	        ApplicationStatusMapper.class
	    }
	)
public abstract class ApplicationSummaryMapper extends BaseMapper<Application, ApplicationSummaryResource, Long>{

    @Mappings({
        @Mapping(source = "applicationStatus.name", target = "applicationStatusName"),
        @Mapping(target="lead", ignore=true),
        @Mapping(target="completedPercentage", ignore=true)
	})
	@Override
	public abstract ApplicationSummaryResource mapToResource(Application domain);
    
    @Override
    public Application mapToDomain(ApplicationSummaryResource resource){
    	return null;
    }
    @Override
    public Iterable<Application> mapToDomain(Iterable<ApplicationSummaryResource> resource){
    	return null;
    }
}
