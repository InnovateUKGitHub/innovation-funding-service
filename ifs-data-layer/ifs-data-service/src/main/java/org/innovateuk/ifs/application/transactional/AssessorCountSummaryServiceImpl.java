package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class AssessorCountSummaryServiceImpl extends BaseTransactionalService implements AssessorCountSummaryService {

    @Autowired
    private ApplicationStatisticsRepository applicationStatisticsRepository;

    @Override
    public ServiceResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, int pageIndex, int pageSize) {

        Pageable pageable = new PageRequest(pageIndex, pageSize);
        Page<AssessorCountSummaryResource> assessorStatistics = applicationStatisticsRepository.getAssessorCountSummaryByCompetition(competitionId, pageable);

        return find(assessorStatistics, notFoundError(Page.class)).andOnSuccessReturn(stats -> new AssessorCountSummaryPageResource(
                assessorStatistics.getTotalElements(),
                assessorStatistics.getTotalPages(),
                assessorStatistics.getContent(),
                assessorStatistics.getNumber(),
                assessorStatistics.getSize()));
    }
}
