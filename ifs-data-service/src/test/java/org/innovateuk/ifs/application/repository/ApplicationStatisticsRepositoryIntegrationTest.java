package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
