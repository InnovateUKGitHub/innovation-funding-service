 package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            OrganisationMapper.class,
            ApplicationMapper.class,
            FileEntryMapper.class,
            FinancialYearAccountsMapper.class
    }
)
public abstract class ApplicationFinanceMapper extends BaseResourceMapper<ApplicationFinance, ApplicationFinanceResource> {

    @Mappings({
        @Mapping(target = "financeOrganisationDetails", ignore = true ),
        @Mapping(source = "application", target = "target")
    })
    public abstract ApplicationFinanceResource mapToResource(ApplicationFinance domain);

}
