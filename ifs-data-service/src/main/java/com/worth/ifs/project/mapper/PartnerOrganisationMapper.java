package com.worth.ifs.project.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
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
