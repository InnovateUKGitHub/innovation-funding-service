package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.Contract;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        config =  GlobalMapperConfig.class,
        uses = {
        },
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class ContractMapper extends BaseMapper<Contract, ContractResource, Long> {

    public Long mapContractToId(Contract object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}
