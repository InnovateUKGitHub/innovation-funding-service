package org.innovateuk.ifs.userorganisation.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserOrganisationResource;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisation;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisationPK;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        OrganisationMapper.class,
        UserMapper.class
    }
)

public abstract class UserOrganisationMapper extends BaseMapper<UserOrganisation, UserOrganisationResource, UserOrganisationPK> {

    @Mappings({
            @Mapping(source = "user.name", target = "name"),
            @Mapping(source = "organisation.name.", target = "organisationName"),
            @Mapping(source = "organisation.id", target = "organisationId"),
            @Mapping(source = "user.email", target = "email"),
            @Mapping(source = "user.status", target = "status")
    })
    @Override
    public abstract UserOrganisationResource mapToResource(UserOrganisation domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "organisation", ignore = true)
    })
    @Override
    public abstract UserOrganisation mapToDomain(UserOrganisationResource resource);

    public UserOrganisationPK mapUserOrganisationToId(UserOrganisation object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}