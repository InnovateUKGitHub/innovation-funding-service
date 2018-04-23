package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.CostTotal;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CostTotalMapper extends BaseMapper<CostTotal, FinanceCostTotalResource, Long> {

    @Mappings({
            @Mapping(target = "financeType", source = "type"),
            @Mapping(target = "financeRowType", source = "name")
    })
    @Override
    public abstract FinanceCostTotalResource mapToResource(CostTotal domain);

    @Mappings({
            @Mapping(target = "type", source = "financeType"),
            @Mapping(target = "name", source = "financeRowType.name")
    })
    @Override
    public abstract CostTotal mapToDomain(FinanceCostTotalResource resource);

    protected FinanceRowType financeRowTypeNameToFinanceRowType(String name) {
        return FinanceRowType.getByName(name).orElseThrow(() -> new IllegalArgumentException("No FinanceRowType " +
                "found for name" + name));
    }
}
