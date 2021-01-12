package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionApplicationConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionApplicationConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionApplicationConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
