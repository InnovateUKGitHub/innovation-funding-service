package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.question.transactional.template.SectionTemplatePersistorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * Transactional component providing functions for persisting copies of a Competition template entity object.
 */
@Component
public class CompetitionTemplatePersistorImpl implements BaseTemplatePersistor<Competition> {
    @Autowired
    private SectionTemplatePersistorImpl sectionTemplateService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
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
