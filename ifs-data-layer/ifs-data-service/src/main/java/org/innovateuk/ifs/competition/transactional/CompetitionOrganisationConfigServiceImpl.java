package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
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

    @Override
    public ServiceResult<Optional<CompetitionOrganisationConfigResource>> findOneByCompetitionId(long competitionId) {

        Optional<CompetitionOrganisationConfig> config = competitionOrganisationConfigRepository.findOneByCompetitionId(competitionId);

        return config.map(competitionOrganisationConfig ->

                serviceSuccess(Optional.of(mapToResource(competitionOrganisationConfig))))
                .orElseGet(() -> serviceSuccess(Optional.empty()));
    }

    public CompetitionOrganisationConfigResource mapToResource(CompetitionOrganisationConfig config) {
        CompetitionOrganisationConfigResource resource = new CompetitionOrganisationConfigResource();
        resource.setId(config.getId());
        resource.setInternationalOrganisationsAllowed(config.getInternationalOrganisationsAllowed());
        resource.setInternationalLeadOrganisationAllowed(config.getInternationalLeadOrganisationAllowed());
        return resource;
    }
}
