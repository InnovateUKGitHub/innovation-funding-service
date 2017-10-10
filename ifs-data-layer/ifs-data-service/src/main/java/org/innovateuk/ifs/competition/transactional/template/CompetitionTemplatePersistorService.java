package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional service providing functions for creating full or partial copies of competition templates.
 */
@Service
public class CompetitionTemplatePersistorService implements BaseTemplatePersistorService<Competition> {
    @Autowired
    private SectionTemplatePersistorService sectionTemplateService;

    @Autowired
    private CompetitionRepository competitionRepository;

    public void cleanByEntityId(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        sectionTemplateService.cleanForPrecedingEntity(competition);
    }

    @Override
    @Transactional
    public Competition persistByEntity(Competition template) {
        sectionTemplateService.persistByPrecedingEntity(template);

        template.setAcademicGrantPercentage(template.getAcademicGrantPercentage());

        return competitionRepository.save(template);
    }
}
