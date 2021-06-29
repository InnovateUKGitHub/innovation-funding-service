package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionExternalConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionExternalConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionExternalConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionExternalConfigResource;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompetitionExternalConfigServiceImpl extends RootTransactionalService implements CompetitionExternalConfigService {

    @Autowired
    private CompetitionExternalConfigRepository competitionExternalConfigRepository;

    @Autowired
    private CompetitionExternalConfigMapper mapper;

    @Override
    public ServiceResult<CompetitionExternalConfigResource> findOneByCompetitionId(long competitionId) {
        return find(competitionExternalConfigRepository.findOneByCompetitionId(competitionId), notFoundError(CompetitionExternalConfig.class, competitionId))
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> update(long competitionId, CompetitionExternalConfigResource competitionExternalConfigResource) {
        return find(competitionExternalConfigRepository.findOneByCompetitionId(competitionId), notFoundError(CompetitionExternalConfig.class, competitionId))
                .andOnSuccessReturnVoid((config) -> {
                    config.setExternalCompetitionId(competitionExternalConfigResource.getExternalCompetitionId());
                });
    }
}
