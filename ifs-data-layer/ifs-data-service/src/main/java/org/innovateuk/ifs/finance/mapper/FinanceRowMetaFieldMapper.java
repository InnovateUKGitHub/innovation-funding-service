package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FinanceRowMetaValueMapper.class
    }
)
public abstract class FinanceRowMetaFieldMapper extends BaseMapper<FinanceRowMetaField, FinanceRowMetaFieldResource, Long> {

    @Override
    @Mappings({
            @Mapping(target = "costValues", ignore = true ),
    })
    public abstract FinanceRowMetaField mapToDomain(FinanceRowMetaFieldResource resource);

    public Long mapCostFieldToId(FinanceRowMetaField object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
