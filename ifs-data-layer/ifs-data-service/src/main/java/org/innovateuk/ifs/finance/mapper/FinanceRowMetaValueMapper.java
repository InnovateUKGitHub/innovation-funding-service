package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaValueResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FinanceRowMetaFieldMapper.class,
        ApplicationFinanceRowMapper.class
    }
)
public abstract class FinanceRowMetaValueMapper extends BaseMapper<FinanceRowMetaValue, FinanceRowMetaValueResource, Long> {

    public Long mapCostValueToId(FinanceRowMetaValue object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
