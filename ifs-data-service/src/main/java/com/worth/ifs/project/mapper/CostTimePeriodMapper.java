package com.worth.ifs.project.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.project.finance.domain.CostTimePeriod;
import com.worth.ifs.project.resource.CostTimePeriodResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CostMapper.class
        }
)
public abstract class CostTimePeriodMapper extends BaseMapper<CostTimePeriod, CostTimePeriodResource, Long> {

    @Override
    public abstract CostTimePeriodResource mapToResource(CostTimePeriod costTimePeriod);

    @Override
    public abstract CostTimePeriod mapToDomain(CostTimePeriodResource costTimePeriodResource);


    public Long mapCostTimePeriodToId(CostTimePeriod object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}