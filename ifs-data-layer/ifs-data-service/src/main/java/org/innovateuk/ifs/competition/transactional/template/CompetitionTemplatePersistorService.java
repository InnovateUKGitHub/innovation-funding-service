package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Transactional service providing functions for creating full or partial copies of competition templates.
 */
@Service
public class CompetitionTemplatePersistorService implements BaseTemplatePersistorService<Competition> {
    @Autowired
    private SectionTemplatePersistorService sectionTemplateService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public void cleanByEntityId(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        // Disabled clean for now as it was causing problems and have to see if we even need it.
        //sectionTemplateService.cleanForPrecedingEntity(competition);
    }

    @Override
    @Transactional
    public Competition persistByEntity(Competition template) {
        entityManager.detach(template);

        Competition competition = competitionRepository.save(template);
        template.setId(competition.getId());
        sectionTemplateService.persistByPrecedingEntity(template);

        template.setAcademicGrantPercentage(template.getAcademicGrantPercentage());

        return competition;
    }
}
