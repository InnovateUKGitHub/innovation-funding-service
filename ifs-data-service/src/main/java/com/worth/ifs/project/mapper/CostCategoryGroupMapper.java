package com.worth.ifs.project.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.project.finance.domain.CostCategoryGroup;
import com.worth.ifs.project.resource.CostCategoryGroupResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CostCategoryMapper.class
        }
)
public abstract class CostCategoryGroupMapper extends BaseMapper<CostCategoryGroup, CostCategoryGroupResource, Long> {
    @Override
    public abstract CostCategoryGroupResource mapToResource(CostCategoryGroup costCategoryGroup);

    @Override
    public abstract CostCategoryGroup mapToDomain(CostCategoryGroupResource costCategoryGroupResource);

    public Long mapCostCategoryGroupToId(CostCategoryGroup object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}