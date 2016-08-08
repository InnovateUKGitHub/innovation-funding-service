package com.worth.ifs.finance.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.CostFieldResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FinanceRowMetaValueMapper.class
    }
)
public abstract class CostFieldMapper extends BaseMapper<CostField, CostFieldResource, Long> {

    public Long mapCostFieldToId(CostField object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}