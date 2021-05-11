package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.period.mapper.AssessmentPeriodMapper;
import org.innovateuk.ifs.assessment.period.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.assessment.period.transactional.AssessmentPeriodServiceImpl;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.competition.builder.AssessmentPeriodBuilder.newAssessmentPeriod;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder.newAssessmentPeriodResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessmentPeriodServiceImplTest extends BaseServiceUnitTest<AssessmentPeriodServiceImpl> {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Mock
    private AssessmentPeriodMapper assessmentPeriodMapper;

    @Override
    protected AssessmentPeriodServiceImpl supplyServiceUnderTest() {
        return new AssessmentPeriodServiceImpl();
    }

    @Test
    public void getAssessmentPeriodByCompetitionId_returnSuccess() {
        Long competitionId = 1L;
        AssessmentPeriod assessmentPeriod = newAssessmentPeriod().build();

        when(assessmentPeriodRepository.findByCompetitionId(competitionId))
                .thenReturn(Collections.singletonList(assessmentPeriod));

        when(assessmentPeriodMapper.mapToResource(assessmentPeriod))
                .thenReturn(newAssessmentPeriodResource().build());

        ServiceResult<List<AssessmentPeriodResource>> result = service.getAssessmentPeriodByCompetitionId(competitionId);

        assertTrue(result.isSuccess());

        verify(assessmentPeriodRepository).findByCompetitionId(competitionId);
        verify(assessmentPeriodMapper).mapToResource(assessmentPeriod);
    }
}
