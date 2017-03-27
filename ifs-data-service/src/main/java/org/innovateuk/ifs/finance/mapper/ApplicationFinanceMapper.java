package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
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
            OrganisationSizeMapper.class
    }
)
public abstract class ApplicationFinanceMapper extends BaseMapper<ApplicationFinance, ApplicationFinanceResource, Long> {

    @Mappings({
        @Mapping(target = "financeOrganisationDetails", ignore = true ),
        @Mapping(source = "application", target = "target")
    })
    @Override
    public abstract ApplicationFinanceResource mapToResource(ApplicationFinance domain);

    @Override
    public abstract ApplicationFinance mapToDomain(ApplicationFinanceResource resource);


    public Long mapApplicationFinanceToId(ApplicationFinance object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
