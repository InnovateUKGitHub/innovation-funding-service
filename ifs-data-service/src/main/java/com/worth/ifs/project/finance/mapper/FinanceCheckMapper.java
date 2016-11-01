package com.worth.ifs.project.finance.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.project.finance.domain.FinanceCheck;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.user.domain.Organisation;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProjectMapper.class,
                OrganisationMapper.class,
                CostGroupMapper.class
        }
)
public abstract class FinanceCheckMapper extends BaseMapper<FinanceCheck, FinanceCheckResource, Long>{
    @Override
    public abstract FinanceCheckResource mapToResource(FinanceCheck domain);

    @Override
    public abstract FinanceCheck mapToDomain(FinanceCheckResource resource);

    public Long mapFinanceCheckToId(FinanceCheck object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
