package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.innovateuk.ifs.workflow.resource.State.*;
import static org.junit.Assert.assertEquals;

public class ApplicationStatisticsRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ApplicationStatisticsRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

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
