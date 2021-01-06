package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionApplicationConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionApplicationConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionApplicationConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompetitionApplicationConfigServiceImpl extends RootTransactionalService implements CompetitionApplicationConfigService {

    @Autowired
    private CompetitionApplicationConfigRepository competitionApplicationConfigRepository;

    @Autowired
    private CompetitionApplicationConfigMapper mapper;

    @Override
    public ServiceResult<CompetitionApplicationConfigResource> findOneByCompetitionId(long competitionId) {
        return find(competitionApplicationConfigRepository.findOneByCompetitionId(competitionId), notFoundError(CompetitionApplicationConfig.class, competitionId))
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> update(long competitionId, CompetitionApplicationConfigResource competitionApplicationConfigResource) {
        return find(competitionApplicationConfigRepository.findOneByCompetitionId(competitionId), notFoundError(CompetitionApplicationConfig.class, competitionId))
                .andOnSuccessReturnVoid((config) -> {
                    config.setMaximumFundingSought(competitionApplicationConfigResource.getMaximumFundingSought());
                    config.setAlwaysOpen(competitionApplicationConfigResource.getAlwaysOpen());

                    config.getCompetition().setCompetitionApplicationConfig(config);
                });
    }
}
