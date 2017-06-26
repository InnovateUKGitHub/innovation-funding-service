package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.mapper.AssessorCountSummaryPageMapper;
import org.innovateuk.ifs.application.repository.AssessorStatisticsRepository;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class AssessorCountSummaryServiceImpl extends BaseTransactionalService implements AssessorCountSummaryService {

    @Autowired
    private AssessorCountSummaryPageMapper assessorCountSummaryPageMapper;

    @Autowired
    private AssessorStatisticsRepository assessorStatisticsRepository;

//    @Override
//    public ServiceResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, int pageIndex, int pageSize, Optional<String> filter) {
//
//        String filterStr = filter.map(String::trim).orElse("");
//        Pageable pageable = new PageRequest(pageIndex, pageSize);
////        Page<AssessorStatistics> assessorStatistics = assessorStatisticsRepository.findByCompetitionParticipantCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, SUBMITTED_STATES, filterStr, pageable);
//        Page<AssessorStatistics> assessorStatistics = assessorStatisticsRepository.findByCompetitionParticipantCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, pageable);
//
//        return find(assessorStatistics, notFoundError(Page.class)).andOnSuccessReturn(stats -> assessorCountSummaryPageMapper.mapToResource(stats));
//    }

    @Override
    public ServiceResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, int pageIndex, int pageSize, Optional<String> filter) {

        String filterStr = filter.map(String::trim).orElse("");
        Pageable pageable = new PageRequest(pageIndex, pageSize);
//        Page<AssessorStatistics> assessorStatistics = assessorStatisticsRepository.findByCompetitionParticipantCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, SUBMITTED_STATES, filterStr, pageable);
        Page<AssessorCountSummaryResource> assessorStatistics = assessorStatisticsRepository.getAssessorCountSummaryByCompetition(competitionId, filterStr, pageable);

        return find(assessorStatistics, notFoundError(Page.class)).andOnSuccessReturn(stats -> new AssessorCountSummaryPageResource(
                assessorStatistics.getTotalElements(),
                assessorStatistics.getTotalPages(),
                assessorStatistics.getContent(),
                assessorStatistics.getNumber(), assessorStatistics.getSize()));
    }
}
