package com.worth.ifs.assessment.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import com.worth.ifs.workflow.mapper.ProcessOutcomeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Maps between domain and resource DTO for {@link com.worth.ifs.assessment.domain.Assessment}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProcessOutcomeMapper.class,
                ProcessRoleMapper.class,
                ApplicationMapper.class
        }
)
public abstract class AssessmentMapper extends BaseMapper<Assessment, AssessmentResource, Long> {

    @Mappings({
            @Mapping(source = "processStatus", target = "status"),
            @Mapping(source = "processEvent", target = "event"),
            @Mapping(source = "version", target = "lastModified"),
            @Mapping(source = "participant.application.id", target = "application"),
            @Mapping(source = "participant.application.competition.id", target = "competition"),
    })
    @Override
    public abstract AssessmentResource mapToResource(Assessment domain);

    @Mappings({
            @Mapping(target = "processStatus", source = "status"),
            @Mapping(target = "processEvent", source = "event")
    })
    @Override
    public abstract Assessment mapToDomain(AssessmentResource resource);

    public Long mapAssessmentToId(Assessment object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}