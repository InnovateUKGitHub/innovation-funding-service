package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.mapper.AssessmentPeriodMapper;
import org.innovateuk.ifs.competition.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodBuilder.newAssessmentPeriod;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder.newAssessmentPeriodResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
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
    public void getAssessmentPeriodByCompetitionIdAndIndex_returnSuccess() {
        Long competitionId = 1L;
        Integer index = 1;

        when(assessmentPeriodRepository.findByCompetitionIdAndIndex(competitionId, index))
                .thenReturn(Optional.of(newAssessmentPeriod().build()));

        when(assessmentPeriodMapper.mapToResource(any(AssessmentPeriod.class)))
                .thenReturn(newAssessmentPeriodResource().build());

        ServiceResult<AssessmentPeriodResource> result = service.getAssessmentPeriodByCompetitionIdAndIndex(competitionId, index);

        assertTrue(result.isSuccess());

        verify(assessmentPeriodRepository).findByCompetitionIdAndIndex(competitionId, index);
        verify(assessmentPeriodMapper).mapToResource(any(AssessmentPeriod.class));
    }

    @Test
    public void getAssessmentPeriodByCompetitionIdAndIndex_returnFailure() {
        Long competitionId = 1L;
        Integer index = 1;

        when(assessmentPeriodRepository.findByCompetitionIdAndIndex(competitionId, index))
                .thenReturn(Optional.empty());

        ServiceResult<AssessmentPeriodResource> result = service.getAssessmentPeriodByCompetitionIdAndIndex(competitionId, index);

        assertTrue(result.isFailure());

        assertNotNull(result.getErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.NOT_FOUND, result.getErrors().get(0).getStatusCode());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), result.getErrors().get(0).getErrorKey());
        assertNotNull(result.getErrors().get(0).getArguments());
        assertEquals("AssessmentPeriodResource not found", result.getErrors().get(0).getArguments().get(0));

        verify(assessmentPeriodRepository).findByCompetitionIdAndIndex(competitionId, index);
    }

    @Test
    public void create_returnSuccess() {
        Long competitionId = 1L;
        Integer index = 1;

        when(competitionRepository.findById(competitionId))
                .thenReturn(Optional.of(newCompetition().build()));

        when(assessmentPeriodRepository.save(any(AssessmentPeriod.class)))
                .thenReturn(newAssessmentPeriod().build());

        when(assessmentPeriodMapper.mapToResource(any(AssessmentPeriod.class)))
                .thenReturn(newAssessmentPeriodResource().build());

        ServiceResult<AssessmentPeriodResource> result = service.create(competitionId, index);

        assertTrue(result.isSuccess());

        verify(competitionRepository).findById(competitionId);
        verify(assessmentPeriodRepository).save(any(AssessmentPeriod.class));
        verify(assessmentPeriodMapper).mapToResource(any(AssessmentPeriod.class));
    }

    @Test
    public void create_returnFailure() {
        Long competitionId = 1L;
        Integer index = 1;

        when(competitionRepository.findById(anyLong())).thenReturn(Optional.empty());

        ServiceResult<AssessmentPeriodResource> result = service.create(competitionId,index);

        assertTrue(result.isFailure());

        assertNotNull(result.getErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.NOT_FOUND, result.getErrors().get(0).getStatusCode());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), result.getErrors().get(0).getErrorKey());
        assertNotNull(result.getErrors().get(0).getArguments());
        assertEquals("Competition not found", result.getErrors().get(0).getArguments().get(0));

        verify(competitionRepository).findById(competitionId);
    }
}
