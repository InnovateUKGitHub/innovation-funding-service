package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.Contract;
import com.worth.ifs.user.resource.ContractResource;
import org.mapstruct.Mapper;

@Mapper(
        config =  GlobalMapperConfig.class,
        uses = {
        }
)
public abstract class ContractMapper extends BaseMapper<Contract, ContractResource, Long> {

    public Long mapContractToId(Contract object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
