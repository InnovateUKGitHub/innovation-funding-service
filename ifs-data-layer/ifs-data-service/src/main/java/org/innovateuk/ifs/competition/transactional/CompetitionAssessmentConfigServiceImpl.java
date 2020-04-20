package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionAssessmentConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionAssessmentConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionAssessmentConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompetitionAssessmentConfigServiceImpl extends RootTransactionalService implements CompetitionAssessmentConfigService {

    @Autowired
    private CompetitionAssessmentConfigRepository competitionAssessmentConfigRepository;

    @Autowired
    private CompetitionAssessmentConfigMapper mapper;

    @Override
    public ServiceResult<CompetitionAssessmentConfigResource> findOneByCompetitionId(long competitionId) {
        return find(competitionAssessmentConfigRepository.findOneByCompetitionId(competitionId), CommonErrors.notFoundError(CompetitionAssessmentConfig.class, competitionId))
                .andOnSuccessReturn(mapper::mapToResource);
    }
    
    @Override
    @Transactional
    @ZeroDowntime(description = "remove setting of data on competition table.", reference = "IFS-7369")
    public ServiceResult<Void> update(long competitionId, CompetitionAssessmentConfigResource competitionAssessmentConfigResource) {
        return find(competitionAssessmentConfigRepository.findOneByCompetitionId(competitionId), CommonErrors.notFoundError(CompetitionAssessmentConfig.class, competitionId))
                .andOnSuccessReturnVoid((config) -> {
                    config.setAverageAssessorScore(competitionAssessmentConfigResource.getAverageAssessorScore());
                    config.setAssessorPay(competitionAssessmentConfigResource.getAssessorPay());
                    config.setAssessorCount(competitionAssessmentConfigResource.getAssessorCount());
                    config.setHasAssessmentPanel(competitionAssessmentConfigResource.getHasAssessmentPanel());
                    config.setHasInterviewStage(competitionAssessmentConfigResource.getHasInterviewStage());
                    config.setAssessorFinanceView(competitionAssessmentConfigResource.getAssessorFinanceView());

                    config.getCompetition().setAssessorPay(competitionAssessmentConfigResource.getAssessorPay());
                    config.getCompetition().setAssessorCount(competitionAssessmentConfigResource.getAssessorCount());
                    config.getCompetition().setHasAssessmentPanel(competitionAssessmentConfigResource.getHasAssessmentPanel());
                    config.getCompetition().setHasAssessmentPanel(competitionAssessmentConfigResource.getHasAssessmentPanel());
                    config.getCompetition().setAssessorFinanceView(competitionAssessmentConfigResource.getAssessorFinanceView());
                });
    }
}
