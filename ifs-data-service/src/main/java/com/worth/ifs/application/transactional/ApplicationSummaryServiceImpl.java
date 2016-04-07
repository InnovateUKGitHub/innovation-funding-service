package com.worth.ifs.application.transactional;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.mapper.ApplicationSummaryMapper;
import com.worth.ifs.application.mapper.ApplicationSummaryPageMapper;
import com.worth.ifs.application.mapper.ClosedCompetitionApplicationSummaryMapper;
import com.worth.ifs.application.mapper.ClosedCompetitionApplicationSummaryPageMapper;
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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationSummaryServiceImpl extends BaseTransactionalService implements ApplicationSummaryService {

	private static final int PAGE_SIZE = 20;
	
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
		competitionSummaryResource.setApplicationsSubmitted(applicationRepository.countByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.SUBMITTED.getId()));
		competitionSummaryResource.setApplicationsNotSubmitted(competitionSummaryResource.getTotalNumberOfApplications() - competitionSummaryResource.getApplicationsSubmitted());
		competitionSummaryResource.setApplicationDeadline(competition.getEndDate());

		return serviceSuccess(competitionSummaryResource);
	}

	private Long getApplicationInProgressCountByCompetitionId(Long competitionId) {

		final List<Application> applications = applicationRepository.findByCompetitionId(competitionId);

		Long inProgressCount = 0l;

		for(Application application : applications){
			final CompletedPercentageResource completedPercentageResource = applicationService.getProgressPercentageByApplicationId(application.getId()).getSuccessObject();
			if(completedPercentageResource.getCompletedPercentage().intValue() > 50 && !(application.getApplicationStatus().equals(ApplicationStatusConstants.SUBMITTED))){
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
				applicationResults = applicationRepository.findByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.SUBMITTED.getId(), pageable);
			} else {
				applicationResults = applicationRepository.findByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.OPEN.getId(), pageable);
			}
			return find(applicationResults, notFoundError(Page.class)).andOnSuccessReturn(closedCompetitionApplicationSummaryPageMapper::mapToResource);
		}
		
		List<Application> resultsList;
		if(submitted) {
			resultsList = applicationRepository.findByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.SUBMITTED.getId());
		} else {
			resultsList = applicationRepository.findByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.OPEN.getId());
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
					if("percentageComplete".equals(sortBy)) {
						return new ApplicationSummaryResourcePercentageCompleteComparator().compare(i1, i2);
					} else if("lead".equals(sortBy)) {
						return new ApplicationSummaryResourceLeadComparator().compare(i1, i2);
					}
					return 0;
				})
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.collect(Collectors.toList());
	}

	private boolean canUseSpringDataPaginationForSummaryResults(String sortBy) {
		return !("percentageComplete".equals(sortBy) || "lead".equals(sortBy));
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
		case "status":
			return new String[]{"applicationStatus.name", "id"};
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
