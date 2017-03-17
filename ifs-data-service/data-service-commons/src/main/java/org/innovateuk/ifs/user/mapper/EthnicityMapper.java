package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.Ethnicity;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

/**
 * Maps between domain and resource DTO for {@link Ethnicity}.
 */
@Mapper(
    config =  GlobalMapperConfig.class,
    uses = {
    },
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class EthnicityMapper extends BaseMapper<Ethnicity, EthnicityResource, Long> {

    public Long mapEthnicityToId(Ethnicity object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
