package com.worth.ifs.project.finance.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.finance.resource.CostCategoryTypeResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CostCategoryGroupMapper.class
        }
)
public abstract class CostCategoryTypeMapper extends BaseMapper<CostCategoryType, CostCategoryTypeResource, Long> {
    @Override
    public abstract CostCategoryTypeResource mapToResource(CostCategoryType costCategoryType);

    @Mappings({
            @Mapping(target = "costCategories", ignore = true)
    })
    @Override
    public abstract CostCategoryType mapToDomain(CostCategoryTypeResource costCategoryTypeResource);


    public Long mapCostCategoryTypeToId(CostCategoryType object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}