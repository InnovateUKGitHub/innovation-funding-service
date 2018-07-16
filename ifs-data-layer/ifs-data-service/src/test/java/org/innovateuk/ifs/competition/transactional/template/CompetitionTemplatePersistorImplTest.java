package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.question.transactional.template.SectionTemplatePersistorImpl;
import org.junit.Test;
import org.mockito.Mock;

import javax.persistence.EntityManager;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompetitionTemplatePersistorImplTest extends BaseServiceUnitTest<CompetitionTemplatePersistorImpl> {

    @Mock
    private SectionTemplatePersistorImpl sectionTemplateService;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private EntityManager entityManagerMock;

    public CompetitionTemplatePersistorImpl supplyServiceUnderTest() {
        return new CompetitionTemplatePersistorImpl(sectionTemplateService, competitionRepository);
    }

    @Test
    public void cleanByEntityId() {
        Competition competition = newCompetition().build();

        when(competitionRepository.findById(competition.getId())).thenReturn(competition);

        service.cleanByEntityId(competition.getId());

        verify(sectionTemplateService).cleanForParentEntity(competition);
    }

    @Test
    public void persistByEntity() {
        Competition competition = newCompetition().build();

        when(competitionRepository.save(competition)).thenReturn(competition);

        service.persistByEntity(competition);

        verify(entityManagerMock).detach(competition);
        verify(competitionRepository).save(competition);
        verify(sectionTemplateService).persistByParentEntity(competition);
    }
}