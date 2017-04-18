package org.innovateuk.ifs.project.financechecks.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.project.financechecks.domain.FinanceCheck;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.project.spendprofile.mapper.CostGroupMapper;
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
