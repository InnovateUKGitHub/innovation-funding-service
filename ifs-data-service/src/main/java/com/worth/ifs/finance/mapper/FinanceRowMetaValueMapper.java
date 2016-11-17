package com.worth.ifs.finance.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.FinanceRowMetaValue;
import com.worth.ifs.finance.resource.FinanceRowMetaValueId;
import com.worth.ifs.finance.resource.FinanceRowMetaValueResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FinanceRowMetaFieldMapper.class,
        ApplicationFinanceRowMapper.class
    }
)
public abstract class FinanceRowMetaValueMapper extends BaseMapper<FinanceRowMetaValue, FinanceRowMetaValueResource, FinanceRowMetaValueId> {

    public FinanceRowMetaValueId mapCostValueToId(FinanceRowMetaValue object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}