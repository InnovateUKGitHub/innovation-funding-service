package org.innovateuk.ifs.project.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProjectMapper.class,
                OrganisationMapper.class
        }
)
public abstract class PartnerOrganisationMapper extends BaseMapper<PartnerOrganisation, PartnerOrganisationResource, Long> {

    @Mappings({
            @Mapping(source = "organisation.name", target = "organisationName")
    })
    @Override
    public abstract  PartnerOrganisationResource mapToResource(PartnerOrganisation domain);

    @Override
    public abstract PartnerOrganisation mapToDomain(PartnerOrganisationResource resource);

    public Long mapPartnerOrganisationToId(PartnerOrganisation object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
