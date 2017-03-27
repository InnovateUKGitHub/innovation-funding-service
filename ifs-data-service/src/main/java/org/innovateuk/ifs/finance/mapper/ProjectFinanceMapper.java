package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            OrganisationMapper.class,
            ProjectMapper.class,
            FileEntryMapper.class,
            OrganisationSizeMapper.class
    }
)
public abstract class ProjectFinanceMapper extends BaseMapper<ProjectFinance, ProjectFinanceResource, Long> {

    @Mappings({
            @Mapping(target = "financeOrganisationDetails", ignore = true ),
            @Mapping(target = "costChanges", ignore = true ),
            @Mapping(source = "project", target = "target")
    })

    @Override
    public abstract ProjectFinanceResource mapToResource(ProjectFinance domain);

    @Mappings({
            @Mapping(target = "viabilityStatus", ignore = true),
            @Mapping(target = "eligibilityStatus", ignore = true),
            @Mapping(target = "creditReportConfirmed", ignore = true)
    })
    @Override
    public abstract ProjectFinance mapToDomain(ProjectFinanceResource projectFinanceResource);

    public Long mapProjectFinanceToId(ProjectFinance object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
