package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionOrganisationConfigMapper {

    public CompetitionOrganisationConfigResource mapToResource(CompetitionOrganisationConfig domain) {

        CompetitionOrganisationConfigResource resource = new CompetitionOrganisationConfigResource();
        resource.setInternationalOrganisationsAllowed(domain.getInternationalOrganisationsAllowed());
        resource.setInternationalLeadOrganisationAllowed(domain.getInternationalLeadOrganisationAllowed());
        return resource;
    }
}
