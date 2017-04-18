package org.innovateuk.ifs.project.spendprofile.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.spendprofile.domain.Cost;
import org.innovateuk.ifs.project.finance.resource.CostResource;
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
