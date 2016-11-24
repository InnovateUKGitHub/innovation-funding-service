package com.worth.ifs.finance.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.finance.domain.ProjectFinance;
import com.worth.ifs.finance.resource.ProjectFinanceResource;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.project.mapper.ProjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            OrganisationMapper.class,
            ProjectMapper.class,
            FileEntryMapper.class
    }
)
public abstract class ProjectFinanceMapper extends BaseMapper<ProjectFinance, ProjectFinanceResource, Long> {

    @Mappings({
        @Mapping(target = "financeOrganisationDetails", ignore = true ),
        @Mapping(source = "project", target = "target")
    })

    @Override
    public abstract ProjectFinanceResource mapToResource(ProjectFinance domain);


    public Long mapProjectFinanceToId(ProjectFinance object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}