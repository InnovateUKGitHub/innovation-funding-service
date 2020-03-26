package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionOrganisationConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionOrganisationConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionOrganisationConfigServiceImpl implements CompetitionOrganisationConfigService{

    @Autowired
    private CompetitionOrganisationConfigRepository competitionOrganisationConfigRepository;

    @Autowired
    private CompetitionOrganisationConfigMapper mapper;

    @Override
    public ServiceResult<Optional<CompetitionOrganisationConfigResource>> findOneByCompetitionId(long competitionId) {

        Optional<CompetitionOrganisationConfig> config = competitionOrganisationConfigRepository.findOneByCompetitionId(competitionId);

        if (config.isPresent()) {
            return serviceSuccess(Optional.of(mapper.mapToResource(config.get())));
        }

        return serviceSuccess(Optional.empty());
    }
}
