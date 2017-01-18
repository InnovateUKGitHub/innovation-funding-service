package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.AffiliationMapper;
import org.innovateuk.ifs.user.mapper.EthnicityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper between {@link User} and {@link AssessorProfileResource}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                AddressMapper.class,
                AffiliationMapper.class,
                EthnicityMapper.class,
                InnovationAreaMapper.class
        }
)
public abstract class AssessorProfileMapper extends BaseMapper<User, AssessorProfileResource, Long> {

    @Mappings({
            @Mapping(source = "profile.skillsAreas", target = "skillsAreas"),
            @Mapping(source = "profile.businessType", target = "businessType"),
            @Mapping(source = "profile.address", target = "address"),
    })
    @Override
    public abstract AssessorProfileResource mapToResource(User domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "inviteName", ignore = true),
            @Mapping(target = "profile", ignore = true),
            @Mapping(target = "organisations", ignore = true),
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "uid", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "processRoles", ignore = true)
    })
    @Override
    public abstract User mapToDomain(AssessorProfileResource resource);
}
