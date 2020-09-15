package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionOrganisationConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionOrganisationConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionOrganisationConfigServiceImpl implements CompetitionOrganisationConfigService {

    @Autowired
    private CompetitionOrganisationConfigRepository competitionOrganisationConfigRepository;

    @Autowired
    private CompetitionOrganisationConfigMapper mapper;

    @Override
    public ServiceResult<CompetitionOrganisationConfigResource> findOneByCompetitionId(long competitionId) {

        Optional<CompetitionOrganisationConfig> config = competitionOrganisationConfigRepository.findOneByCompetitionId(competitionId);

        if (config.isPresent()) {
            return serviceSuccess(mapper.mapToResource(config.get()));
        }

        return serviceSuccess(new CompetitionOrganisationConfigResource(false, false));
    }

    @Override
    @Transactional
    public ServiceResult<Void> update(long competitionId, CompetitionOrganisationConfigResource competitionOrganisationConfigResource) {
        return find(competitionOrganisationConfigRepository.findOneByCompetitionId(competitionId), notFoundError(CompetitionOrganisationConfig.class, competitionId))
                .andOnSuccessReturnVoid((config) -> {
                    config.setInternationalOrganisationsAllowed(competitionOrganisationConfigResource.getInternationalOrganisationsAllowed());
                    if (Boolean.TRUE.equals(config.getInternationalOrganisationsAllowed())) {
                        config.setInternationalLeadOrganisationAllowed(competitionOrganisationConfigResource.getInternationalLeadOrganisationAllowed());
                    } else {
                        config.setInternationalLeadOrganisationAllowed(null);
                    }
                });
    }
}
