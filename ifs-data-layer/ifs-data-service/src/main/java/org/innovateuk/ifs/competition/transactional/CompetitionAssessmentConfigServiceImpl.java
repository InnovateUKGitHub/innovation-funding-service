package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionAssessmentConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionAssessmentConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionAssessmentConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionAssessmentConfigServiceImpl implements CompetitionAssessmentConfigService {

    @Autowired
    private CompetitionAssessmentConfigRepository competitionAssessmentConfigRepository;

    @Autowired
    private CompetitionAssessmentConfigMapper mapper;

    @Override
    public ServiceResult<CompetitionAssessmentConfigResource> findOneByCompetitionId(long competitionId) {
        Optional<CompetitionAssessmentConfig> config = competitionAssessmentConfigRepository.findOneByCompetitionId(competitionId);

        if (config.isPresent()) {
            return serviceSuccess(mapper.mapToResource(config.get()));
        }

        // test this with just one set default
        return serviceSuccess(new CompetitionAssessmentConfigResource(false));
//        return serviceSuccess(new CompetitionAssessmentConfigResource(false, 1, BigDecimal.ZERO, true, true, AssessorFinanceView.DETAILED));
    }

    @Override
    @Transactional
    public ServiceResult<Void> update(long competitionId, CompetitionAssessmentConfigResource competitionAssessmentConfigResource) {
        Optional<CompetitionAssessmentConfig> config = competitionAssessmentConfigRepository.findOneByCompetitionId(competitionId);

        if (config.isPresent()) {
            config.get().setAverageAssessorScore(competitionAssessmentConfigResource.getAverageAssessorScore());
            config.get().setAssessorCount(competitionAssessmentConfigResource.getAssessorCount());
            config.get().setHasAssessmentPanel(competitionAssessmentConfigResource.getHasAssessmentPanel());
            config.get().setHasInterviewStage(competitionAssessmentConfigResource.getHasInterviewStage());
            config.get().setAssessorFinanceView(competitionAssessmentConfigResource.getAssessorFinanceView());
        }

        return serviceSuccess();
    }
}
