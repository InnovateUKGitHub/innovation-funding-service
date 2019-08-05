package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.template.CompetitionTemplatePersistorImpl;
import org.innovateuk.ifs.competitionsetup.domain.AssessorCountOption;
import org.innovateuk.ifs.competitionsetup.repository.AssessorCountOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

/**
 * Service that can create Competition template copies
 */
@Service
public class CompetitionSetupTemplateServiceImpl implements CompetitionSetupTemplateService {

    @Autowired
    private CompetitionTemplatePersistorImpl competitionTemplatePersistor;

    @Autowired
    private AssessorCountOptionRepository assessorCountOptionRepository;

    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public ServiceResult<Competition> initializeCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId) {
        Optional<CompetitionType> competitionType = competitionTypeRepository.findById(competitionTypeId);

        if (!competitionType.isPresent()) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        Competition template = competitionType.get().getTemplate();
        if (template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        Optional<Competition> competitionOptional = competitionRepository.findById(competitionId);
        if (!competitionOptional.isPresent() || competitionIsNotInSetupState(competitionOptional.get())) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        Competition competition = competitionOptional.get();

        competition.setCompetitionType(competitionType.get());
        competition = setDefaultAssessorPayAndCount(competition);

        competitionTemplatePersistor.cleanByEntityId(competitionId);

        competition = copyTemplatePropertiesToCompetition(template, competition);
        competition = initialiseFinanceTypes(competition);

        return serviceSuccess(competitionTemplatePersistor.persistByEntity(competition));
    }

    private Competition copyTemplatePropertiesToCompetition(Competition template, Competition competition) {
        competition.setSections(new ArrayList<>(template.getSections()));
        competition.setGrantClaimMaximums(new ArrayList<>(template.getGrantClaimMaximums()));
        competition.setTermsAndConditions(template.getTermsAndConditions());
        competition.setAcademicGrantPercentage(template.getAcademicGrantPercentage());
        competition.setMinProjectDuration(template.getMinProjectDuration());
        competition.setMaxProjectDuration(template.getMaxProjectDuration());
        competition.setApplicationFinanceType(template.getApplicationFinanceType());
        return competition;
    }

    private Competition setDefaultAssessorPayAndCount(Competition competition) {
        if (competition.getAssessorCount() == null) {
            Optional<AssessorCountOption> defaultAssessorOption = assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competition.getCompetitionType().getId());
            defaultAssessorOption.ifPresent(assessorCountOption -> competition.setAssessorCount(assessorCountOption.getOptionValue()));
        }

        if (competition.getAssessorPay() == null) {
            competition.setAssessorPay(CompetitionSetupServiceImpl.DEFAULT_ASSESSOR_PAY);
        }
        return competition;
    }

    private boolean competitionIsNotInSetupState(Competition competition) {
        return !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP);
    }

    private Competition initialiseFinanceTypes(Competition competition) {
        switch (competition.getFundingType()) {
            case GRANT:
                competition.getFinanceRowTypes().addAll(EnumSet.of(
                        LABOUR,
                        OVERHEADS,
                        MATERIALS,
                        CAPITAL_USAGE,
                        SUBCONTRACTING_COSTS,
                        TRAVEL,
                        OTHER_COSTS,
                        FINANCE,
                        OTHER_FUNDING
                ));
                break;
            case LOAN:
                competition.getFinanceRowTypes().addAll(EnumSet.of(
                        LABOUR,
                        OVERHEADS,
                        MATERIALS,
                        CAPITAL_USAGE,
                        SUBCONTRACTING_COSTS,
                        TRAVEL,
                        OTHER_COSTS,
                        GRANT_CLAIM_AMOUNT,
                        OTHER_FUNDING
                ));
                break;
            case PROCUREMENT:
                competition.getFinanceRowTypes().addAll(EnumSet.of(
                        LABOUR,
                        //OVERHEADS,
                        MATERIALS,
                        CAPITAL_USAGE,
                        SUBCONTRACTING_COSTS,
                        TRAVEL,
                        OTHER_COSTS,
                        FINANCE,
                        OTHER_FUNDING
                ));
                break;
        }
        return competition;
    }

}
