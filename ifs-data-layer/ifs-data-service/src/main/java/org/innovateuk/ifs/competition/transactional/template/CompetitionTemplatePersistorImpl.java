package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.question.transactional.template.SectionTemplatePersistorImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Transactional component providing functions for persisting copies of a Competition template entity object.
 */
@Component
public class CompetitionTemplatePersistorImpl implements BaseTemplatePersistor<Competition> {

    private SectionTemplatePersistorImpl sectionTemplateService;
    private CompetitionRepository competitionRepository;

    public CompetitionTemplatePersistorImpl(SectionTemplatePersistorImpl sectionTemplateService,
                                            CompetitionRepository competitionRepository) {
        this.sectionTemplateService = sectionTemplateService;
        this.competitionRepository = competitionRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void cleanByEntityId(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        sectionTemplateService.cleanForParentEntity(competition);
    }

    @Override
    @Transactional
    public Competition persistByEntity(Competition template) {
        entityManager.detach(template);

        Competition competition = competitionRepository.save(template);
        template.setId(competition.getId());
        sectionTemplateService.persistByParentEntity(template);

        return competition;
    }
}
