package com.worth.ifs.application.transactional;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.mapper.ApplicationSummaryMapper;
import com.worth.ifs.application.mapper.ApplicationSummaryPageMapper;
import com.worth.ifs.application.mapper.ClosedCompetitionApplicationSummaryMapper;
import com.worth.ifs.application.mapper.ClosedCompetitionApplicationSummaryPageMapper;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.application.resource.PageResource;
import com.worth.ifs.application.resource.comparators.ApplicationSummaryResourceLeadComparator;
import com.worth.ifs.application.resource.comparators.ApplicationSummaryResourcePercentageCompleteComparator;
import com.worth.ifs.application.resource.comparators.ClosedCompetitionApplicationSummaryGrantRequestedComparator;
import com.worth.ifs.application.resource.comparators.ClosedCompetitionApplicationSummaryLeadComparator;
import com.worth.ifs.application.resource.comparators.ClosedCompetitionApplicationSummaryNumberOfPartnersComparator;
import com.worth.ifs.application.resource.comparators.ClosedCompetitionApplicationSummaryTotalProjectCostComparator;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.transactional.BaseTransactionalService;

@Service
public class ApplicationSummaryServiceImpl extends BaseTransactionalService implements ApplicationSummaryService {

	private static final int PAGE_SIZE = 20;
	
	private static final Collection<Long> SUBMITTED_STATUS_IDS = Arrays.asList(
			ApplicationStatusConstants.APPROVED.getId(),
			ApplicationStatusConstants.REJECTED.getId(),
			ApplicationStatusConstants.SUBMITTED.getId());

	@Autowired
    private ApplicationSummaryMapper applicationSummaryMapper;
	
	@Autowired
	private ApplicationSummaryPageMapper applicationSummaryPageMapper;

	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private ClosedCompetitionApplicationSummaryMapper closedCompetitionApplicationSummaryMapper;
	
	@Autowired
	private ClosedCompetitionApplicationSummaryPageMapper closedCompetitionApplicationSummaryPageMapper;
	
	@Override
	public ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, int pageIndex, String sortBy) {
		
		String[] sortField = getApplicationSummarySortField(sortBy);
		Pageable pageable = new PageRequest(pageIndex, PAGE_SIZE, new Sort(Direction.ASC, sortField));
		
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
		competitionSummaryResource.setApplicationsStarted(applicationRepository.countByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.OPEN.getId()));
		competitionSummaryResource.setApplicationsInProgress(getApplicationInProgressCountByCompetitionId(competitionId));
		competitionSummaryResource.setApplicationsSubmitted(applicationRepository.countByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS));
		competitionSummaryResource.setApplicationsNotSubmitted(competitionSummaryResource.getTotalNumberOfApplications() - competitionSummaryResource.getApplicationsSubmitted());
		competitionSummaryResource.setApplicationDeadline(competition.getEndDate());

		return serviceSuccess(competitionSummaryResource);
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
	public ServiceResult<ClosedCompetitionApplicationSummaryPageResource> getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(
			Long competitionId, int pageIndex, String sortBy) {
		return getClosedCompetitionApplicationSummariesByCompetitionId(competitionId, pageIndex, sortBy, true);
	}
	
	@Override
	public ServiceResult<ClosedCompetitionApplicationSummaryPageResource> getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(
			Long competitionId, int pageIndex, String sortBy) {
		return getClosedCompetitionApplicationSummariesByCompetitionId(competitionId, pageIndex, sortBy, false);
	}
	
	private ServiceResult<ClosedCompetitionApplicationSummaryPageResource> getClosedCompetitionApplicationSummariesByCompetitionId(
			Long competitionId, int pageIndex, String sortBy, boolean submitted) {
		String[] sortField = getClosedCompetitionApplicationSummarySortField(sortBy);
		Pageable pageable = new PageRequest(pageIndex, PAGE_SIZE, new Sort(Direction.ASC, sortField));
		
		if(canUseSpringDataPaginationForClosedCompetitionResults(sortBy)){
			Page<Application> applicationResults;
			if(submitted) {
				applicationResults = applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS, pageable);
			} else {
				applicationResults = applicationRepository.findByCompetitionIdAndApplicationStatusIdNotIn(competitionId, SUBMITTED_STATUS_IDS, pageable);
			}
			return find(applicationResults, notFoundError(Page.class)).andOnSuccessReturn(closedCompetitionApplicationSummaryPageMapper::mapToResource);
		}
		
		List<Application> resultsList;
		if(submitted) {
			resultsList = applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS);
		} else {
			resultsList = applicationRepository.findByCompetitionIdAndApplicationStatusIdNotIn(competitionId, SUBMITTED_STATUS_IDS);
		}
		
		ClosedCompetitionApplicationSummaryPageResource result = new ClosedCompetitionApplicationSummaryPageResource();
		result.setContent(closedCompetitionSortAndRestrictResults(resultsList, pageable, sortBy));
		
		return pageFromUnsortedApplicationResults(result, resultsList, pageable, sortBy, ClosedCompetitionApplicationSummaryPageResource.class);
	}
	
	@Override
	public List<Application> getApplicationSummariesByCompetitionIdAndStatus(Long competitionId, Long applicationStatusId) {
		List<Application> applicationResults = applicationRepository.findByCompetitionIdAndApplicationStatusId(competitionId, applicationStatusId);
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

	private String[] getClosedCompetitionApplicationSummarySortField(String sortBy) {
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
	
	private boolean canUseSpringDataPaginationForClosedCompetitionResults(String sortBy) {
		return !("numberOfPartners".equals(sortBy) || "lead".equals(sortBy) | "grantRequested".equals(sortBy) || "totalProjectCost".equals(sortBy));
	}
	
	private List<ClosedCompetitionApplicationSummaryResource> closedCompetitionSortAndRestrictResults(List<Application> resultsList, Pageable pageable, String sortBy) {
		return resultsList.stream()
				.map(closedCompetitionApplicationSummaryMapper::mapToResource)
				.sorted((i1, i2) -> {
					if("numberOfPartners".equals(sortBy)) {
						return new ClosedCompetitionApplicationSummaryNumberOfPartnersComparator().compare(i1, i2);
					} else if("lead".equals(sortBy)) {
						return new ClosedCompetitionApplicationSummaryLeadComparator().compare(i1, i2);
					} else if("grantRequested".equals(sortBy)) {
						return new ClosedCompetitionApplicationSummaryGrantRequestedComparator().compare(i1, i2);
					} else if("totalProjectCost".equals(sortBy)) {
						return new ClosedCompetitionApplicationSummaryTotalProjectCostComparator().compare(i1, i2);
					}
					return 0;
				})
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.collect(Collectors.toList());
	}

}
