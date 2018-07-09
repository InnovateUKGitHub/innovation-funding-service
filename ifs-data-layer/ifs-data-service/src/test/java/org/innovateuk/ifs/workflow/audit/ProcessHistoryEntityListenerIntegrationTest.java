package org.innovateuk.ifs.workflow.audit;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.resource.ApplicationState.CREATED;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;

public class ProcessHistoryEntityListenerIntegrationTest extends BaseRepositoryIntegrationTest<ApplicationRepository> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @PersistenceContext
    private EntityManager entityManager; // avoiding having a ProcessHistoryRepository as we don't want it in application code

    @Autowired
    @Override
    protected void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }

    @Before
    public void loginForAuditFields() {
        loginSteveSmith();
    }

    @Test
    public void preUpdate() {
        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);

        Application application = newApplication()
                .withCompetition(competition)
                .withActivityState(CREATED, SUBMITTED)
                .with(id(null))
                .build();

        repository.save(application);
        flushAndClearSession();

        assertEquals(1, repository.countByCompetitionId(competition.getId()));

        ProcessHistory processHistory =
                entityManager.createQuery(
                        "select h from ProcessHistory h join fetch h.process p join fetch p.target t",
                        ProcessHistory.class
                ).getSingleResult();

        assertEquals(application.getApplicationProcess().getId(), processHistory.getProcess().getId());
        assertEquals(ApplicationState.CREATED.getStateName(), processHistory.getProcessStateName());
    }
}