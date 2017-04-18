package org.innovateuk.ifs.project.spendprofile.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.spendprofile.domain.CostCategoryGroup;
import org.innovateuk.ifs.project.finance.resource.CostCategoryGroupResource;
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
