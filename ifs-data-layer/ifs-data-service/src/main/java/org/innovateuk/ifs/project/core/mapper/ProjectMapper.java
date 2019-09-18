package org.innovateuk.ifs.project.core.mapper;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.documents.mapper.ProjectDocumentsMapper;
import org.innovateuk.ifs.project.financereviewer.domain.FinanceReviewer;
import org.innovateuk.ifs.project.financereviewer.repository.FinanceReviewerRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                AddressMapper.class,
                ApplicationMapper.class,
                ProjectUserMapper.class,
                FileEntryMapper.class,
                ProjectDocumentsMapper.class
        }
)
public abstract class ProjectMapper extends BaseMapper<Project, ProjectResource, Long> {

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @Autowired
    private MonitoringOfficerRepository projectMonitoringOfficerRepository;

    @Autowired
    private FinanceReviewerRepository financeReviewerRepository;

    @Mappings({
            @Mapping(target = "projectState", ignore = true),
            @Mapping(target = "competition", source = "application.competition.id"),
            @Mapping(target = "competitionName", source = "application.competition.name"),
            @Mapping(target = "monitoringOfficerUser", source = "projectMonitoringOfficerOrElseNull.user.id")
    })
    @Override
    public abstract ProjectResource mapToResource(Project project);

    @Mappings({
            @Mapping(target = "organisations", ignore = true),
            @Mapping(target = "partnerOrganisations", ignore = true),
            @Mapping(target = "spendProfiles", ignore = true)
    })
    @Override
    public abstract Project mapToDomain(ProjectResource projectResource);


    @AfterMapping
    public void setAdditionalFieldsOnResource(Project project, @MappingTarget ProjectResource resource) {
        ProjectProcess process = projectProcessRepository.findOneByTargetId(project.getId());

        if (process != null) {
            resource.setProjectState(process.getProcessState());
        }
    }

    public Long mapProjectToId(Project object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Long mapProjectMonitoringOfficerUserToId(Optional<MonitoringOfficer> object) {
        return object == null ? null : object.map(ProjectParticipant::getId).orElse(null);
    }

    public MonitoringOfficer mapProjectMonitoringOfficerIdUserToDomain(Long id) {
        return id == null ? null : projectMonitoringOfficerRepository.findById(id).get();
    }

    public FinanceReviewer mapFinanceReviewerIdUserToDomain(Long id) {
        return id == null ? null : financeReviewerRepository.findById(id).get();
    }

    public Long mapFinanceReviewerUserToId(FinanceReviewer financeReviewer) {
        return financeReviewer == null ? null : financeReviewer.getId();
    }

}