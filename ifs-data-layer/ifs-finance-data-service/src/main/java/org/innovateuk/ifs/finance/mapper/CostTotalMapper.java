package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.CostTotal;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CostTotalMapper extends BaseMapper<CostTotal, FinanceCostTotalResource, Long> {

    @Mapping(target = "financeType", source = "type")
    @Override
    public abstract FinanceCostTotalResource mapToResource(CostTotal domain);

    @Mapping(target = "type", source = "financeType")
    @Override
    public abstract CostTotal mapToDomain(FinanceCostTotalResource resource);
}
