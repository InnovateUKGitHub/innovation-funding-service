package org.innovateuk.ifs.project.core.mapper;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                AddressMapper.class,
                ApplicationMapper.class,
                ProjectUserMapper.class,
                FileEntryMapper.class
        }
)
public abstract class ProjectMapper extends BaseMapper<Project, ProjectResource, Long> {

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @Mappings({
            @Mapping(target = "projectState", ignore = true)
    })
    @Override
    public abstract ProjectResource mapToResource(Project project);

    @Mappings({
            @Mapping(target = "organisations", ignore = true),
            // TODO DW - for now, exclude partner organisations from mapper - include later though as will be really
            // useful
            @Mapping(target = "partnerOrganisations", ignore = true),
            @Mapping(target = "spendProfiles", ignore = true),
    })
    @Override
    public abstract Project mapToDomain(ProjectResource projectResource);


    @AfterMapping
    public void setAdditionalFieldsOnResource(Project project, @MappingTarget ProjectResource resource) {
        ProjectProcess process = projectProcessRepository.findOneByTargetId(project.getId());

        if (process != null) {
            resource.setProjectState(process.getActivityState());
        }
    }

    public Long mapProjectToId(Project object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
