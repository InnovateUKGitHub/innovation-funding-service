package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Transactional service providing functions for creating full or partial copies of competition templates.
 */
@Service
public class CompetitionSetupTemplateServiceImpl extends BaseTransactionalService implements CompetitionSetupTemplateService {
    @Autowired
    private SectionTemplateService sectionTemplateService;

    @Override
    @Transactional
    public ServiceResult<Competition> createCompetitionByCompetitionTemplate(Competition competition, Competition template) {
        sectionTemplateService.cleanForCompetition(competition);

        if (competition == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if (template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        List<Section> sectionsWithoutParentSections = template.getSections().stream()
                .filter(s -> s.getParentSection() == null)
                .collect(Collectors.toList());

        sectionTemplateService.attachSectionRecursively(competition, sectionsWithoutParentSections, null);

        competition.setAcademicGrantPercentage(template.getAcademicGrantPercentage());

        return serviceSuccess(competitionRepository.save(competition));
    }
}
