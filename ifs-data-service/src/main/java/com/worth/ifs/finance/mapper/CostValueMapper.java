package com.worth.ifs.finance.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.domain.CostValueId;
import com.worth.ifs.finance.resource.CostValueResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CostFieldMapper.class,
        CostMapper.class
    }
)
public abstract class CostValueMapper extends BaseMapper<CostValue, CostValueResource, CostValueId> {

    public CostValueId mapCostValueToId(CostValue object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}