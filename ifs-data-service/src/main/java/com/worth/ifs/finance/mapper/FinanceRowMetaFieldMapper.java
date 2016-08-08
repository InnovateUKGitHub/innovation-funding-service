package com.worth.ifs.finance.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.FinanceRowMetaField;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FinanceRowMetaValueMapper.class
    }
)
public abstract class FinanceRowMetaFieldMapper extends BaseMapper<FinanceRowMetaField, FinanceRowMetaFieldResource, Long> {

    public Long mapCostFieldToId(FinanceRowMetaField object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}