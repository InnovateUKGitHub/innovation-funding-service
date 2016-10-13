package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.Ethnicity;
import com.worth.ifs.user.resource.EthnicityResource;
import org.mapstruct.Mapper;

/**
 * Maps between domain and resource DTO for {@link Ethnicity}.
 */
@Mapper(
    config =  GlobalMapperConfig.class,
    uses = {
    }
)
public abstract class EthnicityMapper extends BaseMapper<Ethnicity, EthnicityResource, Long> {

    public Long mapEthnicityToId(Ethnicity object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
