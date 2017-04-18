package org.innovateuk.ifs.project.spendprofile.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.spendprofile.domain.CostTimePeriod;
import org.innovateuk.ifs.project.finance.resource.CostTimePeriodResource;
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
