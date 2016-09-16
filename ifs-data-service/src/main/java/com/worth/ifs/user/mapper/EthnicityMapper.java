package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.user.domain.Ethnicity;
import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
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
