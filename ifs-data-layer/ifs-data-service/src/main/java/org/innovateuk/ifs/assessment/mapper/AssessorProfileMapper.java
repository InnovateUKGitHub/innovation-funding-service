package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.user.mapper.AffiliationMapper;
import org.innovateuk.ifs.user.mapper.EthnicityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper between {@link Profile} and {@link ProfileResource}.
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
public abstract class AssessorProfileMapper extends BaseMapper<Profile, ProfileResource, Long> {
    @Mappings({
            @Mapping(target = "affiliations", ignore = true),
    })
    @Override
    public abstract ProfileResource mapToResource(Profile domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "agreement", ignore = true),
            @Mapping(target = "agreementSignedDate", ignore = true),
    })
    @Override
    public abstract Profile mapToDomain(ProfileResource resource);
}
