package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
        },
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class AgreementMapper extends BaseMapper<Agreement, AgreementResource, Long> {

    public Long mapAgreementToId(Agreement object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}
