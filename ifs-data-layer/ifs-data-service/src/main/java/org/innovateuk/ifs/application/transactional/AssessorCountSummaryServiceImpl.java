package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class AssessorCountSummaryServiceImpl extends BaseTransactionalService implements AssessorCountSummaryService {

    public static final Set<State> REJECTED_AND_SUBMITTED_ASSESSMENT_STATES =
            simpleMapSet(EnumSet.of(REJECTED, WITHDRAWN, SUBMITTED), AssessmentStates::getBackingState);
    public static final Set<State> NOT_ACCEPTED_OR_SUBMITTED_ASSESSMENT_STATES =
            simpleMapSet(EnumSet.of(PENDING, REJECTED, WITHDRAWN, CREATED, SUBMITTED), AssessmentStates::getBackingState);
    public static final Set<State> SUBMITTED_ASSESSMENT_STATES = EnumSet.of(SUBMITTED.getBackingState());

    @Autowired
    private ApplicationStatisticsRepository applicationStatisticsRepository;

    @Override
    public ServiceResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, int pageIndex, int pageSize) {

        Pageable pageable = new PageRequest(pageIndex, pageSize);
        Page<AssessorCountSummaryResource> assessorStatistics =
                applicationStatisticsRepository.getAssessorCountSummaryByCompetition(competitionId, null, null, pageable);

        return find(assessorStatistics, notFoundError(Page.class)).andOnSuccessReturn(stats -> new AssessorCountSummaryPageResource(
                assessorStatistics.getTotalElements(),
                assessorStatistics.getTotalPages(),
                assessorStatistics.getContent(),
                assessorStatistics.getNumber(),
                assessorStatistics.getSize()));
    }
}