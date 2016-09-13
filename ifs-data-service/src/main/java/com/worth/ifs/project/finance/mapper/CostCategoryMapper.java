package com.worth.ifs.project.finance.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.resource.CostCategoryResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CostCategoryGroupMapper.class
        }
)
public abstract class CostCategoryMapper extends BaseMapper<CostCategory, CostCategoryResource, Long> {
    @Override
    public abstract CostCategoryResource mapToResource(CostCategory costCategory);

    @Override
    public abstract CostCategory mapToDomain(CostCategoryResource costCategoryResource);


    public Long mapCostCategoryToId(CostCategory object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}