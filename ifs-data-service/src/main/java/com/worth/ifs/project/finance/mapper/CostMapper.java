package com.worth.ifs.project.finance.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.project.finance.domain.Cost;
import com.worth.ifs.project.finance.resource.CostResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CostGroupMapper.class,
                CostTimePeriodMapper.class,
                CostCategoryMapper.class
        }
)
public abstract class CostMapper extends BaseMapper<Cost, CostResource, Long> {

    @Override
    public abstract CostResource mapToResource(Cost cost);

    @Override
    public abstract Cost mapToDomain(CostResource costResource);

    public Long mapCostToId(Cost object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}