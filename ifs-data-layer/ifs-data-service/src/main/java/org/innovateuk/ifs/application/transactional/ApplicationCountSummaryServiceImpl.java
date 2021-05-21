package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.mapper.ApplicationCountSummaryPageMapper;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
public class ApplicationCountSummaryServiceImpl extends BaseTransactionalService implements ApplicationCountSummaryService {

    @Autowired
    private ApplicationCountSummaryPageMapper applicationCountSummaryPageMapper;

    @Autowired
    private ApplicationStatisticsRepository applicationStatisticsRepository;

    private static final Map<String, Sort> SORT_FIELD_TO_DB_SORT_FIELDS;

    static {
        Map<String, Sort> sortFieldToDbSortFields = new HashMap<>();
        sortFieldToDbSortFields.put("id", Sort.by(ASC, "id"));
        sortFieldToDbSortFields.put("appTitle", Sort.by(ASC, "name", "id"));
        sortFieldToDbSortFields.put("leadOrg", Sort.by(ASC, "leadOrganisation", "id"));
        sortFieldToDbSortFields.put("assignedApps", Sort.by(ASC, "assessors", "id"));
        sortFieldToDbSortFields.put("completedApps", Sort.by(ASC, "submitted", "id"));

        SORT_FIELD_TO_DB_SORT_FIELDS = Collections.unmodifiableMap(sortFieldToDbSortFields);
    }

    @ZeroDowntime(reference = "IFS-8853", description = "This can probably be removed")
    @Override
    public ServiceResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(long competitionId,
                                                                                                          int pageIndex,
                                                                                                          int pageSize,
                                                                                                          Optional<String> filter) {

        String filterStr = filter.map(String::trim).orElse("");
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<ApplicationStatistics> applicationStatistics = applicationStatisticsRepository.findByCompetitionAndApplicationProcessActivityStateIn(competitionId, SUBMITTED_STATES, filterStr, pageable);

        return find(applicationStatistics, notFoundError(Page.class)).andOnSuccessReturn(stats -> applicationCountSummaryPageMapper.mapToResource(stats));
    }

    @Override
    public ServiceResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionIdAndAssessmentPeriodId(long competitionId,
                                                                                                          long assessmentPeriodId,
                                                                                                          int pageIndex,
                                                                                                          int pageSize,
                                                                                                          Optional<String> filter) {

        String filterStr = filter.map(String::trim).orElse("");
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<ApplicationStatistics> applicationStatistics = applicationStatisticsRepository.findByCompetitionAndApplicationProcessActivityStateInAndAssessmentPeriodIn(competitionId, assessmentPeriodId, SUBMITTED_STATES, filterStr, pageable);

        return find(applicationStatistics, notFoundError(Page.class)).andOnSuccessReturn(stats -> applicationCountSummaryPageMapper.mapToResource(stats));
    }

    @Override
    public ServiceResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionIdAndAssessorId(
            long competitionId,
            long assessorId,
            int page,
            int size,
            ApplicationCountSummaryResource.Sort sort,
            String filter) {
        Pageable pageable = PageRequest.of(page, size, getApplicationSummarySortField(sort));

        Page<ApplicationCountSummaryResource> result =
                applicationStatisticsRepository.findStatisticsForApplicationsNotAssignedTo(competitionId, assessorId, filter, pageable);

        return serviceSuccess(new ApplicationCountSummaryPageResource(result.getTotalElements(), result.getTotalPages(), result.getContent(), result.getNumber(), result.getSize()));
    }

    @Override
    public ServiceResult<List<Long>> getApplicationIdsByCompetitionIdAndAssessorId(long competitionId, long assessorId, String filter) {
       return serviceSuccess(applicationStatisticsRepository.findApplicationIdsNotAssignedTo(competitionId, assessorId, filter));
    }

    private Sort getApplicationSummarySortField(ApplicationCountSummaryResource.Sort sort) {
        switch(sort) {
            case APPLICATION_NUMBER:
                return Sort.by(ASC, "id");
            case TITLE:
                return Sort.by(ASC, "name");
            case LEAD_ORGANISATION:
                return Sort.by(ASC, "lead.name");
            case ASSESSORS:
                return JpaSort.unsafe(ASC, ApplicationStatisticsRepository.SUM_ASSESSORS);
            case ACCEPTED:
                return JpaSort.unsafe(ASC, ApplicationStatisticsRepository.SUM_ACCEPTED);
            case SUBMITTED:
                return JpaSort.unsafe(ASC, ApplicationStatisticsRepository.SUM_SUBMITTED);
        }
        throw new IFSRuntimeException("Unknown sort option: " + sort.name());
    }

    private Sort getApplicationSummarySortField(String sortBy) {
        Sort result = SORT_FIELD_TO_DB_SORT_FIELDS.get(sortBy);
        return result != null ? result : SORT_FIELD_TO_DB_SORT_FIELDS.get("id");
    }
}
