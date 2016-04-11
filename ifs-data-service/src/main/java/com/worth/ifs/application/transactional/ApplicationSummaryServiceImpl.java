package com.worth.ifs.application.transactional;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.mapper.*;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.application.resource.comparators.*;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationSummaryServiceImpl extends BaseTransactionalService implements ApplicationSummaryService {

	private static final int PAGE_SIZE = 20;

	public static final Collection<Long> SUBMITTED_STATUS_IDS = Arrays.asList(
			ApplicationStatusConstants.APPROVED.getId(),
			ApplicationStatusConstants.REJECTED.getId(),
			ApplicationStatusConstants.SUBMITTED.getId());

	private static final Collection<Long> CREATED_AND_OPEN_STATUS_IDS = Arrays.asList(
			ApplicationStatusConstants.CREATED.getId(),
			ApplicationStatusConstants.OPEN.getId());

	@Autowired
    private ApplicationSummaryMapper applicationSummaryMapper;
	
	@Autowired
	private ApplicationSummaryPageMapper applicationSummaryPageMapper;

	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private ClosedCompetitionSubmittedApplicationSummaryMapper closedCompetitionSubmittedApplicationSummaryMapper;
	
	@Autowired
	private ClosedCompetitionSubmittedApplicationSummaryPageMapper closedCompetitionSubmittedApplicationSummaryPageMapper;
	
	@Autowired
	private ClosedCompetitionNotSubmittedApplicationSummaryMapper closedCompetitionNotSubmittedApplicationSummaryMapper;
	
	@Autowired
	private ClosedCompetitionNotSubmittedApplicationSummaryPageMapper closedCompetitionNotSubmittedApplicationSummaryPageMapper;
	
	@Override
	public ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, int pageIndex, String sortBy) {
		
		String[] sortField = getApplicationSummarySortField(sortBy);
		Pageable pageable = new PageRequest(pageIndex, PAGE_SIZE, new Sort(Sort.Direction.ASC, sortField));
		
		if(canUseSpringDataPaginationForSummaryResults(sortBy)){
			Page<Application> applicationResults = applicationRepository.findByCompetitionId(competitionId, pageable);
			return find(applicationResults, notFoundError(Page.class)).andOnSuccessReturn(applicationSummaryPageMapper::mapToResource);
		}
		
		List<Application> resultsList = applicationRepository.findByCompetitionId(competitionId);
		
		ApplicationSummaryPageResource result = new ApplicationSummaryPageResource();
		result.setContent(sortAndRestrictSummaryResults(resultsList, pageable, sortBy));
		
		return pageFromUnsortedApplicationResults(result, resultsList, pageable, sortBy, ApplicationSummaryPageResource.class);
	}

	@Override
	public ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId){
		Competition competition = competitionRepository.findById(competitionId);

		CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
		competitionSummaryResource.setCompetitionId(competitionId);
		competitionSummaryResource.setCompetitionName(competition.getName());
		competitionSummaryResource.setCompetitionStatus(competition.getCompetitionStatus());
		competitionSummaryResource.setTotalNumberOfApplications(applicationRepository.countByCompetitionId(competitionId));
		competitionSummaryResource.setApplicationsStarted(getApplicationStartedCountByCompetitionId(competitionId));
		competitionSummaryResource.setApplicationsInProgress(getApplicationInProgressCountByCompetitionId(competitionId));
		competitionSummaryResource.setApplicationsSubmitted(applicationRepository.countByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS));
		competitionSummaryResource.setApplicationsNotSubmitted(competitionSummaryResource.getTotalNumberOfApplications() - competitionSummaryResource.getApplicationsSubmitted());
		competitionSummaryResource.setApplicationDeadline(competition.getEndDate());

		return serviceSuccess(competitionSummaryResource);
	}

	private long getApplicationStartedCountByCompetitionId(Long competitionId){
		applicationRepository.countByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.OPEN.getId());

		Long startedCount = 0L;

		final List<Application> applications = applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, CREATED_AND_OPEN_STATUS_IDS);

		for(Application application : applications){
			final CompletedPercentageResource completedPercentageResource = applicationService.getProgressPercentageByApplicationId(application.getId()).getSuccessObject();
			if(completedPercentageResource.getCompletedPercentage().intValue() <= 50) {
				startedCount++;
			}
		}

		return startedCount;
	}

	private Long getApplicationInProgressCountByCompetitionId(Long competitionId) {

		Long inProgressCount = 0L;

		final List<Application> applications = applicationRepository.findByCompetitionIdAndApplicationStatusIdNotIn(competitionId, SUBMITTED_STATUS_IDS);

		for(Application application : applications){
			final CompletedPercentageResource completedPercentageResource = applicationService.getProgressPercentageByApplicationId(application.getId()).getSuccessObject();
			if(completedPercentageResource.getCompletedPercentage().intValue() > 50) {
				inProgressCount++;
			}
		}

		return inProgressCount;
	}

	@Override
	public ServiceResult<ClosedCompetitionSubmittedApplicationSummaryPageResource> getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(
			Long competitionId, int pageIndex, String sortBy) {
		String[] sortField = getClosedCompetitionSubmittedApplicationSummarySortField(sortBy);
		Pageable pageable = new PageRequest(pageIndex, PAGE_SIZE, new Sort(Sort.Direction.ASC, sortField));
		
		if(canUseSpringDataPaginationForClosedCompetitionSubmittedResults(sortBy)){
			Page<Application> applicationResults = applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS, pageable);
			return find(applicationResults, notFoundError(Page.class)).andOnSuccessReturn(closedCompetitionSubmittedApplicationSummaryPageMapper::mapToResource);
		}
		
		List<Application> resultsList = applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS);
		
		ClosedCompetitionSubmittedApplicationSummaryPageResource result = new ClosedCompetitionSubmittedApplicationSummaryPageResource();
		result.setContent(closedCompetitionSubmittedSortAndRestrictResults(resultsList, pageable, sortBy));
		
		return pageFromUnsortedApplicationResults(result, resultsList, pageable, sortBy, ClosedCompetitionSubmittedApplicationSummaryPageResource.class);
	}
	
	@Override
	public ServiceResult<ClosedCompetitionNotSubmittedApplicationSummaryPageResource> getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(
			Long competitionId, int pageIndex, String sortBy) {
		String[] sortField = getClosedCompetitionNotSubmittedApplicationSummarySortField(sortBy);
		Pageable pageable = new PageRequest(pageIndex, PAGE_SIZE, new Sort(Sort.Direction.ASC, sortField));
		
		if(canUseSpringDataPaginationForClosedCompetitionNotSubmittedResults(sortBy)){
			Page<Application> applicationResults = applicationRepository.findByCompetitionIdAndApplicationStatusIdNotIn(competitionId, SUBMITTED_STATUS_IDS, pageable);
			return find(applicationResults, notFoundError(Page.class)).andOnSuccessReturn(closedCompetitionNotSubmittedApplicationSummaryPageMapper::mapToResource);
		}
		
		List<Application> resultsList = applicationRepository.findByCompetitionIdAndApplicationStatusIdNotIn(competitionId, SUBMITTED_STATUS_IDS);
		
		ClosedCompetitionNotSubmittedApplicationSummaryPageResource result = new ClosedCompetitionNotSubmittedApplicationSummaryPageResource();
		result.setContent(closedCompetitionNotSubmittedSortAndRestrictResults(resultsList, pageable, sortBy));
		
		return pageFromUnsortedApplicationResults(result, resultsList, pageable, sortBy, ClosedCompetitionNotSubmittedApplicationSummaryPageResource.class);
	}
	
	
	@Override
	public List<Application> getApplicationSummariesByCompetitionIdAndStatus(Long competitionId, Collection<Long> applicationStatusId) {
		List<Application> applicationResults = applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, applicationStatusId);
		return applicationResults;
	}

	private <U, T extends PageResource<U>> ServiceResult<T> pageFromUnsortedApplicationResults(T result, List<Application> resultsList, Pageable pageable, String sortBy, Class clazz) {
		result.setNumber(pageable.getPageNumber());
		result.setSize(pageable.getPageSize());
		result.setTotalElements(resultsList.size());
		result.setTotalPages((resultsList.size() / pageable.getPageSize()) + 1);
		return find(result, notFoundError(clazz));
	}
	
	private List<ApplicationSummaryResource> sortAndRestrictSummaryResults(List<Application> resultsList, Pageable pageable, String sortBy) {
		return resultsList.stream()
				.map(applicationSummaryMapper::mapToResource)
				.sorted((i1, i2) -> {
					if("id".equals(sortBy)) {
						return 0;
					} else if("lead".equals(sortBy)) {
						return new ApplicationSummaryResourceLeadComparator().compare(i1, i2);
					}
					return new ApplicationSummaryResourcePercentageCompleteComparator().compare(i1, i2);
				})
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.collect(Collectors.toList());
	}

	private boolean canUseSpringDataPaginationForSummaryResults(String sortBy) {
		return "id".equals(sortBy) || "name".equals(sortBy);
	}

	private String[] getApplicationSummarySortField(String sortBy) {
		if(StringUtils.isEmpty(sortBy)){
			return new String[]{"id"};
		}
		
		switch (sortBy) {
		case "id":
			return new String[]{"id"};
		case "name":
			return new String[]{"name", "id"};
		default:
			return new String[]{"id"};
		}
	}

	
	private String[] getClosedCompetitionSubmittedApplicationSummarySortField(String sortBy) {
		if(StringUtils.isEmpty(sortBy)){
			return new String[]{"id"};
		}
		
		switch (sortBy) {
		case "id":
			return new String[]{"id"};
		case "name":
			return new String[]{"name", "id"};
		case "duration":
			return new String[]{"durationInMonths", "id"};
		default:
			return new String[]{"id"};
		}
	}
	
	private String[] getClosedCompetitionNotSubmittedApplicationSummarySortField(String sortBy) {
		if(StringUtils.isEmpty(sortBy)){
			return new String[]{"id"};
		}
		
		switch (sortBy) {
		case "id":
			return new String[]{"id"};
		case "name":
			return new String[]{"name", "id"};
		default:
			return new String[]{"id"};
		}
	}
	
	private boolean canUseSpringDataPaginationForClosedCompetitionSubmittedResults(String sortBy) {
		return !("numberOfPartners".equals(sortBy) || "lead".equals(sortBy) | "grantRequested".equals(sortBy) || "totalProjectCost".equals(sortBy));
	}
	
	private boolean canUseSpringDataPaginationForClosedCompetitionNotSubmittedResults(String sortBy) {
		return "id".equals(sortBy) || "name".equals(sortBy);
	}
	
	private List<ClosedCompetitionSubmittedApplicationSummaryResource> closedCompetitionSubmittedSortAndRestrictResults(List<Application> resultsList, Pageable pageable, String sortBy) {
		return resultsList.stream()
				.map(closedCompetitionSubmittedApplicationSummaryMapper::mapToResource)
				.sorted((i1, i2) -> {
					if("numberOfPartners".equals(sortBy)) {
						return new ClosedCompetitionSubmittedApplicationSummaryNumberOfPartnersComparator().compare(i1, i2);
					} else if("lead".equals(sortBy)) {
						return new ClosedCompetitionSubmittedApplicationSummaryLeadComparator().compare(i1, i2);
					} else if("grantRequested".equals(sortBy)) {
						return new ClosedCompetitionSubmittedApplicationSummaryGrantRequestedComparator().compare(i1, i2);
					} else if("totalProjectCost".equals(sortBy)) {
						return new ClosedCompetitionSubmittedApplicationSummaryTotalProjectCostComparator().compare(i1, i2);
					}
					return 0;
				})
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.collect(Collectors.toList());
	}
	
	private List<ClosedCompetitionNotSubmittedApplicationSummaryResource> closedCompetitionNotSubmittedSortAndRestrictResults(List<Application> resultsList, Pageable pageable, String sortBy) {
		return resultsList.stream()
				.map(closedCompetitionNotSubmittedApplicationSummaryMapper::mapToResource)
				.sorted((i1, i2) -> {
					if("id".equals(sortBy)) {
						return 0;
					} else if("lead".equals(sortBy)) {
						return new ClosedCompetitionNotSubmittedApplicationSummaryLeadComparator().compare(i1, i2);
					}
					return new ClosedCompetitionNotSubmittedApplicationSummaryPercentageCompleteComparator().compare(i1, i2);
				})
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.collect(Collectors.toList());
	}

}
