package org.innovateuk.ifs.project.spendprofile.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.spendprofile.domain.CostGroup;
import org.innovateuk.ifs.project.finance.resource.CostGroupResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CostMapper.class
        }
)
public abstract class CostGroupMapper extends BaseMapper<CostGroup, CostGroupResource, Long> {

    @Override
    public abstract CostGroupResource mapToResource(CostGroup costGroup);

    @Override
    public abstract CostGroup mapToDomain(CostGroupResource costGroupResource);


    public Long mapCostGroupToId(CostGroup object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
