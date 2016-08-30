package com.worth.ifs.project.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.project.finance.domain.CostGroup;
import com.worth.ifs.project.resource.CostGroupResource;
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