package com.worth.ifs.finance.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.user.mapper.OrganisationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            OrganisationMapper.class,
            ApplicationMapper.class
    }
)
public abstract class ApplicationFinanceMapper extends BaseMapper<ApplicationFinance, ApplicationFinanceResource, Long> {

    @Mappings({
        @Mapping(target = "financeOrganisationDetails", ignore = true )
    })
    public abstract ApplicationFinanceResource mapToResource(ApplicationFinance domain);


    public Long mapApplicationFinanceToId(ApplicationFinance object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}