package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaValueResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FinanceRowMetaFieldMapper.class
    }
)
public abstract class FinanceRowMetaValueMapper extends BaseMapper<FinanceRowMetaValue, FinanceRowMetaValueResource, Long> {

}
