package com.worth.ifs.assessment.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import com.worth.ifs.user.mapper.UserMapper;
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
                ApplicationMapper.class,
                UserMapper.class
        }
)
public abstract class AssessmentMapper extends BaseMapper<Assessment, AssessmentResource, Long> {

    @Mappings({
            @Mapping(source = "processEvent", target = "event"),
            @Mapping(source = "version", target = "lastModified"),
            @Mapping(source = "participant.id", target = "processRole"),
            @Mapping(source = "target.id", target = "application"),
            @Mapping(source = "target.competition.id", target = "competition"),
            @Mapping(source = "activityState", target = "assessmentState"),
    })
    @Override
    public abstract AssessmentResource mapToResource(Assessment domain);

    @Mappings({
            @Mapping(target = "processEvent", source = "event"),
            @Mapping(target = "participant", source = "processRole"),
            @Mapping(target = "target", source = "application"),
            @Mapping(target = "activityState", source = "assessmentState", ignore = true)
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