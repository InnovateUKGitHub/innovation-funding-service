package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionAverageAssessorScoreConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionAverageAssessorScoreConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionAverageAssessorScoreConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionAverageAssessorScoreConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionAverageAssessorScoreConfigServiceImpl implements CompetitionAverageAssessorScoreConfigService {

    @Autowired
    private CompetitionAverageAssessorScoreConfigRepository competitionAverageAssessorScoreConfigRepository;

    @Autowired
    private CompetitionAverageAssessorScoreConfigMapper mapper;

    @Override
    public ServiceResult<CompetitionAverageAssessorScoreConfigResource> findOneByCompetitionId(long competitionId) {
        Optional<CompetitionAverageAssessorScoreConfig> config = competitionAverageAssessorScoreConfigRepository.findOneByCompetitionId(competitionId);

        if (config.isPresent()) {
            return serviceSuccess(mapper.mapToResource(config.get()));
        }

        return serviceSuccess(new CompetitionAverageAssessorScoreConfigResource(false));
    }
}
