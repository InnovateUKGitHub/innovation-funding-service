package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ApplicationStatisticsRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ApplicationStatisticsRepository> {

    @Autowired
    @Override
    protected void setRepository(ApplicationStatisticsRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByCompetition() throws Exception {
        Long competitionId = 1L;

        List<ApplicationStatistics> statisticsList = repository.findByCompetition(competitionId);
        assertEquals(6, statisticsList.size());

    }

    @Test
    public void findByCompetitionPaged() throws Exception {
        Long competitionId = 1L;

        Pageable pageable = new PageRequest(1, 3);

        Page<ApplicationStatistics> statisticsPage = repository.findByCompetition(competitionId, "", pageable);
        assertEquals(6, statisticsPage.getTotalElements());
        assertEquals(3, statisticsPage.getSize());
        assertEquals(1, statisticsPage.getNumber());
    }

    @Test
    public void findByCompetitionFilterd() throws Exception {
        Long competitionId = 1L;

        Pageable pageable = new PageRequest(0, 20);

        Page<ApplicationStatistics> statisticsPage = repository.findByCompetition(competitionId, "4", pageable);
        assertEquals(1, statisticsPage.getTotalElements());
        assertEquals(20, statisticsPage.getSize());
        assertEquals(0, statisticsPage.getNumber());
    }
}
