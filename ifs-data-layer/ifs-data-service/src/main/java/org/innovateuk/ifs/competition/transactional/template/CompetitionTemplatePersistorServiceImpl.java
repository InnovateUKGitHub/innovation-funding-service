package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Transactional service providing functions for creating full or partial copies of competition templates.
 */
@Service
public class CompetitionTemplatePersistorServiceImpl implements BaseTemplatePersistorService<Competition> {
    @Autowired
    private SectionTemplatePersistorService sectionTemplateService;

    @Autowired
    private CompetitionRepository competitionRepository;

    public ServiceResult<Competition> prepareTemplateAndCreateByTemplate(Competition competition, Competition competitionTemplate) {
        if (competition == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if (competitionTemplate == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        competitionTemplate.setId(competition.getId());
        return serviceSuccess(persistByEntity(competitionTemplate));
    }

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
