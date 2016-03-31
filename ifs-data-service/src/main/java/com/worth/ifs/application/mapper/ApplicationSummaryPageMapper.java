package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
    config = GlobalMapperConfig.class
)
public abstract class ApplicationSummaryPageMapper {

	@Autowired
	private ApplicationSummaryMapper applicationSummaryMapper;
	
	public ApplicationSummaryPageResource mapToResource(Page<Application> source){
		ApplicationSummaryPageResource result = new ApplicationSummaryPageResource();
		result.setNumber(source.getNumber());
		result.setSize(source.getSize());
		result.setTotalElements(source.getTotalElements());
		result.setTotalPages(source.getTotalPages());
		result.setContent(convertApplications(source.getContent()));
		return result;
	}

	private List<ApplicationSummaryResource> convertApplications(List<Application> content) {
		return content.stream().map(applicationSummaryMapper::mapToResource).collect(Collectors.toList());
	}

}
