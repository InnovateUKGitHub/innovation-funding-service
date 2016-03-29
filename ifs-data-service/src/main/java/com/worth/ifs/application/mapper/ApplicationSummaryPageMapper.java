package com.worth.ifs.application.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;

@Mapper(
	    config = GlobalMapperConfig.class,
		uses = {
			ApplicationSummaryMapper.class
	    }
	)
public abstract class ApplicationSummaryPageMapper extends BaseMapper<Page<Application>, ApplicationSummaryPageResource, Long>{

	@Override
	public ApplicationSummaryPageResource mapToResource(Page<Application> domain){
		return null; // TODO this is what i need to do some mapping. could i just implement it?  this mapstruct thing seems like a huge faff.
	}

	@Override
	public Iterable<ApplicationSummaryPageResource> mapToResource(Iterable<Page<Application>> domain) {
		return null;
	}
	
	@Override
    public Page<Application> mapToDomain(ApplicationSummaryPageResource resource){
    	return null;
    }
    @Override
    public Iterable<Page<Application>> mapToDomain(Iterable<ApplicationSummaryPageResource> resource){
    	return null;
    }
}
