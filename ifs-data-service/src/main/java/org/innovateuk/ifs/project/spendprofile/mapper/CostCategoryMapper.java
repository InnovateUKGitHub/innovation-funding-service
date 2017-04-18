package org.innovateuk.ifs.project.spendprofile.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.spendprofile.domain.CostCategory;
import org.innovateuk.ifs.project.finance.resource.CostCategoryResource;
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
