package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.util.EncodingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository.SORT_BY_FIRSTNAME;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class AssessorCountSummaryServiceImpl extends BaseTransactionalService implements AssessorCountSummaryService {

    @Autowired
    private ApplicationStatisticsRepository applicationStatisticsRepository;

    @Override
    public ServiceResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, String assessorNameFilter, int pageIndex, int pageSize) {

        Pageable pageable = PageRequest.of(pageIndex, pageSize, SORT_BY_FIRSTNAME);

        assessorNameFilter = EncodingUtils.urlDecode(assessorNameFilter);

        Page<AssessorCountSummaryResource> assessorStatistics =
                applicationStatisticsRepository.getAssessorCountSummaryByCompetitionAndAssessorNameLike(competitionId, assessorNameFilter, pageable);

        return find(assessorStatistics, notFoundError(Page.class)).andOnSuccessReturn(stats -> new AssessorCountSummaryPageResource(
                assessorStatistics.getTotalElements(),
                assessorStatistics.getTotalPages(),
                assessorStatistics.getContent(),
                assessorStatistics.getNumber(),
                assessorStatistics.getSize()));
    }
}