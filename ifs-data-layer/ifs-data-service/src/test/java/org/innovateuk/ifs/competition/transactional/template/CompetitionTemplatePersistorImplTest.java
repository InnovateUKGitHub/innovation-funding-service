package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Test;
import org.mockito.Mock;

import javax.persistence.EntityManager;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompetitionTemplatePersistorImplTest extends BaseServiceUnitTest<CompetitionTemplatePersistorImpl> {
    public CompetitionTemplatePersistorImpl supplyServiceUnderTest() {
        return new CompetitionTemplatePersistorImpl();
    }

    @Mock
    private SectionTemplatePersistorImpl sectionTemplateService;

    @Mock
    private EntityManager entityManagerMock;

    @Test
    public void cleanByEntityId() throws Exception {
        Long competitionId = 2L;

        Competition competition = newCompetition().build();

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);

        service.cleanByEntityId(competitionId);

        verify(sectionTemplateService).cleanForParentEntity(competition);
    }

    @Test
    public void persistByEntity() throws Exception {
        Competition template = newCompetition().build();

        Competition savedCompetition = template;
        savedCompetition.setId(3L);

        when(competitionRepositoryMock.save(template)).thenReturn(savedCompetition);

        service.persistByEntity(template);

        verify(entityManagerMock).detach(template);
        verify(competitionRepositoryMock).save(template);
        verify(sectionTemplateService).persistByParentEntity(refEq(savedCompetition));
    }

}