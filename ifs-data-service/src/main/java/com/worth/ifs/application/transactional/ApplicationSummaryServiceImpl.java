package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.mapper.ApplicationSummaryMapper;
import com.worth.ifs.application.mapper.ApplicationSummaryPageMapper;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationSummaryServiceImpl extends BaseTransactionalService implements ApplicationSummaryService {

	private static final int PAGE_SIZE = 20;
	
	@Autowired
    private ApplicationSummaryMapper applicationSummaryMapper;
	
	@Autowired
	private ApplicationSummaryPageMapper applicationSummaryPageMapper;
	
	@Override
	public ServiceResult<ApplicationSummaryResource> getApplicationSummaryById(Long id) {
		return getApplication(id).andOnSuccessReturn(applicationSummaryMapper::mapToResource);
	}

	@Override
	public ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, int pageIndex, String sortBy) {
		
		String[] sortField = getSortField(sortBy);
		Pageable pageable = new PageRequest(pageIndex, PAGE_SIZE, new Sort(Direction.ASC, sortField));
		
		if(canUseSpringDataPagination(sortBy)){
			Page<Application> applicationResults = applicationRepository.findByCompetitionId(competitionId, pageable);
			return find(applicationResults, notFoundError(Page.class)).andOnSuccessReturn(applicationSummaryPageMapper::mapToResource);
		}
		
		List<Application> resultsList = applicationRepository.findByCompetitionId(competitionId);
		return pageFromUnsortedApplicationResults(resultsList, pageable, sortBy);
	}

	@Override
	public List<Application> getApplicationSummariesByCompetitionIdAndStatus(Long competitionId, Long applicationStatusId) {
		List<Application> applicationResults = applicationRepository.findByCompetitionIdAndApplicationStatusId(competitionId, applicationStatusId);
		return applicationResults;
	}

	private ServiceResult<ApplicationSummaryPageResource> pageFromUnsortedApplicationResults(List<Application> resultsList, Pageable pageable, String sortBy) {
		List<ApplicationSummaryResource> content = sortAndRestrictResults(resultsList, pageable, sortBy);
		ApplicationSummaryPageResource result = new ApplicationSummaryPageResource();
		result.setContent(content);
		result.setNumber(pageable.getPageNumber());
		result.setSize(pageable.getPageSize());
		result.setTotalElements(resultsList.size());
		result.setTotalPages((resultsList.size() / pageable.getPageSize()) + 1);
		return find(result, notFoundError(ApplicationSummaryPageResource.class));
	}

	private List<ApplicationSummaryResource> sortAndRestrictResults(List<Application> resultsList, Pageable pageable, String sortBy) {
		return resultsList.stream()
				.map(applicationSummaryMapper::mapToResource)
				.sorted((i1, i2) -> {
					if("percentageComplete".equals(sortBy)) {
						return compareByPercentageComplete(i1, i2);
					} else if("lead".equals(sortBy)) {
						return compareByLead(i1, i2);
					}
					return 0;
				})
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.collect(Collectors.toList());
	}

	private int compareByLead(ApplicationSummaryResource i1, ApplicationSummaryResource i2) {
		if(i1.getLead() == null) {
			if(i2.getLead() == null) {
				return 0;
			}
			return -1;
		}
		if(i2.getLead() == null) {
			return 1;
		}
		
		int leadComparison = i1.getLead().compareTo(i2.getLead());
		if(leadComparison == 0){
			return i1.getId().compareTo(i2.getId());
		}
		return leadComparison;
	}

	private int compareByPercentageComplete(ApplicationSummaryResource i1, ApplicationSummaryResource i2) {
		if(i1.getCompletedPercentage() == null) {
			if(i2.getCompletedPercentage() == null) {
				return 0;
			}
			return -1;
		}
		if(i2.getCompletedPercentage() == null) {
			return 1;
		}
		int percentageComparison = i1.getCompletedPercentage().compareTo(i2.getCompletedPercentage());
		if(percentageComparison == 0){
			return i1.getId().compareTo(i2.getId());
		}
		return percentageComparison;
	}

	private boolean canUseSpringDataPagination(String sortBy) {
		return !("percentageComplete".equals(sortBy) || "lead".equals(sortBy));
	}

	private String[] getSortField(String sortBy) {
		if(StringUtils.isEmpty(sortBy)){
			return new String[]{"id"};
		}
		
		switch (sortBy) {
		case "id":
			return new String[]{"id"};
		case "name":
			return new String[]{"name", "id"};
		case "status":
			return new String[]{"applicationStatus.name", "id"};
		default:
			return new String[]{"id"};
		}
	}

}
