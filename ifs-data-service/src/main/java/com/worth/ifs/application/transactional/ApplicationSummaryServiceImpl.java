package com.worth.ifs.application.transactional;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.mapper.ApplicationSummaryMapper;
import com.worth.ifs.application.mapper.ApplicationSummaryPageMapper;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.application.resource.comparators.ApplicationSummaryResourceGrantRequestedComparator;
import com.worth.ifs.application.resource.comparators.ApplicationSummaryResourceLeadComparator;
import com.worth.ifs.application.resource.comparators.ApplicationSummaryResourceNumberOfPartnersComparator;
import com.worth.ifs.application.resource.comparators.ApplicationSummaryResourcePercentageCompleteComparator;
import com.worth.ifs.application.resource.comparators.ApplicationSummaryResourceTotalProjectCostComparator;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;

@Service
public class ApplicationSummaryServiceImpl extends BaseTransactionalService implements ApplicationSummaryService {

	public static final Collection<Long> SUBMITTED_STATUS_IDS = Arrays.asList(
			ApplicationStatusConstants.APPROVED.getId(),
			ApplicationStatusConstants.REJECTED.getId(),
			ApplicationStatusConstants.SUBMITTED.getId());

	public static final Collection<Long> CREATED_AND_OPEN_STATUS_IDS = Arrays.asList(
			ApplicationStatusConstants.CREATED.getId(),
			ApplicationStatusConstants.OPEN.getId());
	
	private static final Map<String, String[]> SORT_FIELD_TO_DB_SORT_FIELDS = new HashMap<String, String[]>() {{
		put("name", new String[]{"name", "id"});
		put("duration", new String[]{"durationInMonths", "id"});
	}};
	
	private static final Map<String, Comparator<ApplicationSummaryResource>> SUMMARY_COMPARATORS = new HashMap<String, Comparator<ApplicationSummaryResource>>(){{
		put("lead", new ApplicationSummaryResourceLeadComparator());
		put("percentageComplete", new ApplicationSummaryResourcePercentageCompleteComparator());
		put("numberOfPartners", new ApplicationSummaryResourceNumberOfPartnersComparator());
		put("grantRequested", new ApplicationSummaryResourceGrantRequestedComparator());
		put("totalProjectCost", new ApplicationSummaryResourceTotalProjectCostComparator());
	}};
	
	private static final Collection<String> FIELDS_NOT_SORTABLE_IN_DB = SUMMARY_COMPARATORS.keySet();
	
	@Autowired
    private ApplicationSummaryMapper applicationSummaryMapper;
	
	@Autowired
	private ApplicationSummaryPageMapper applicationSummaryPageMapper;

	
	@Override
	public ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, String sortBy, int pageIndex, int pageSize) {
		
		return applicationSummaries(sortBy, pageIndex, pageSize,
				pageable -> applicationRepository.findByCompetitionId(competitionId, pageable),
				() -> applicationRepository.findByCompetitionId(competitionId));
	}
	
	@Override
	public ServiceResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(
			Long competitionId, String sortBy, int pageIndex, int pageSize) {
		
		return applicationSummaries(sortBy, pageIndex, pageSize, 
				pageable -> applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS, pageable),
				() -> applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS));
	}
	
	@Override
	public ServiceResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(
			Long competitionId, String sortBy, int pageIndex, int pageSize) {
		
		return applicationSummaries(sortBy, pageIndex, pageSize,
				pageable -> applicationRepository.findByCompetitionIdAndApplicationStatusIdNotIn(competitionId, SUBMITTED_STATUS_IDS, pageable),
				() -> applicationRepository.findByCompetitionIdAndApplicationStatusIdNotIn(competitionId, SUBMITTED_STATUS_IDS));
	}
	
	private ServiceResult<ApplicationSummaryPageResource> applicationSummaries(String sortBy, int pageIndex, int pageSize, Function<Pageable, Page<Application>> paginatedApplicationsSupplier, Supplier<List<Application>> nonPaginatedApplicationsSupplier) {
		String[] sortField = getApplicationSummarySortField(sortBy);
		Pageable pageable = new PageRequest(pageIndex, pageSize, new Sort(Sort.Direction.ASC, sortField));
		
		if(canUseSpringDataPaginationForSummaryResults(sortBy)){
			Page<Application> applicationResults = paginatedApplicationsSupplier.apply(pageable);
			return find(applicationResults, notFoundError(Page.class)).andOnSuccessReturn(applicationSummaryPageMapper::mapToResource);
		}
		
		List<Application> resultsList = nonPaginatedApplicationsSupplier.get();
		
		ApplicationSummaryPageResource result = new ApplicationSummaryPageResource();
		result.setContent(sortAndRestrictSummaryResults(resultsList, pageable, sortBy));
		
		result.setNumber(pageable.getPageNumber());
		result.setSize(pageable.getPageSize());
		result.setTotalElements(resultsList.size());
		result.setTotalPages((resultsList.size() / pageable.getPageSize()) + 1);
		return find(result, notFoundError(ApplicationSummaryPageResource.class));
	}
	
	private List<ApplicationSummaryResource> sortAndRestrictSummaryResults(List<Application> resultsList, Pageable pageable, String sortBy) {
		return resultsList.stream()
				.map(applicationSummaryMapper::mapToResource)
				.sorted((i1, i2) -> {
					Comparator<ApplicationSummaryResource> comparatorForField = SUMMARY_COMPARATORS.get(sortBy);
					if(comparatorForField == null) {
						return 0;
					}
					return comparatorForField.compare(i1, i2);
				})
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.collect(Collectors.toList());
	}

	private boolean canUseSpringDataPaginationForSummaryResults(String sortBy) {
		return !FIELDS_NOT_SORTABLE_IN_DB.stream().anyMatch((field) -> field.equals(sortBy));
	}

	private String[] getApplicationSummarySortField(String sortBy) {
		String[] result =  SORT_FIELD_TO_DB_SORT_FIELDS.get(sortBy);
		if(result == null) {
			return new String[]{"id"};
		}
		return result;
	}
	
}
