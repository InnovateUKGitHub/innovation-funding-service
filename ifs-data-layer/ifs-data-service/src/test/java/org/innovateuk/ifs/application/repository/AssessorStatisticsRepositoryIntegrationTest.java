package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.AssessorStatistics;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertEquals;

public class AssessorStatisticsRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AssessorStatisticsRepository> {

    public static final Collection<State> SUBMITTED_STATUSES = simpleMap(asList(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED,
            ApplicationState.SUBMITTED), ApplicationState::getBackingState);

    @Autowired
    @Override
    protected void setRepository(AssessorStatisticsRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByCompetition() throws Exception {
        Long competitionId = 1L;

//        List<AssessorStatistics> statisticsList = repository.findByCompetitionParticipantCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, SUBMITTED_STATUSES);
        List<AssessorStatistics> statisticsList = repository.findByCompetitionParticipantCompetitionId(competitionId);
        assertEquals(5, statisticsList.size());

    }

    @Test
    public void findByCompetitionPaged() throws Exception {
        Long competitionId = 1L;

        Pageable pageable = new PageRequest(1, 3);

        Page<AssessorStatistics> statisticsPage = repository.findByCompetitionParticipantCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId,  pageable);
//        Page<AssessorStatistics> statisticsPage = repository.findByCompetitionParticipantCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, SUBMITTED_STATUSES, "", pageable);
        assertEquals(5, statisticsPage.getTotalElements());
        assertEquals(3, statisticsPage.getSize());
        assertEquals(1, statisticsPage.getNumber());
    }

    @Test
    public void getAssessorCountSummaryByCompetition() throws Exception {
        Long competitionId = 1L;

        Pageable pageable = new PageRequest(1, 3);

        Page<AssessorCountSummaryResource> statisticsPage = repository.getAssessorCountSummaryByCompetition(competitionId, "", pageable);
//        Page<AssessorStatistics> statisticsPage = repository.findByCompetitionParticipantCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, SUBMITTED_STATUSES, "", pageable);
        assertEquals(5, statisticsPage.getTotalElements());
        assertEquals(3, statisticsPage.getSize());
        assertEquals(1, statisticsPage.getNumber());
    }


    //
//    @Test
//    public void findByCompetitionFiltered() throws Exception {
//        Long competitionId = 1L;
//
//        Pageable pageable = new PageRequest(0, 20);
//
//        Page<AssessorStatistics> statisticsPage = repository.findByCompetitionParticipantCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, SUBMITTED_STATUSES,"4", pageable);
//        assertEquals(1, statisticsPage.getTotalElements());
//        assertEquals(20, statisticsPage.getSize());
//        assertEquals(0, statisticsPage.getNumber());
//    }
}
