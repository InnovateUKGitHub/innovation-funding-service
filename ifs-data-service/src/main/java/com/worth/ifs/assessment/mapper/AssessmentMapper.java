package com.worth.ifs.assessment.mapper;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import com.worth.ifs.workflow.mapper.ProcessOutcomeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProcessOutcomeMapper.class,
                ProcessRoleMapper.class
        }
)
public abstract class AssessmentMapper extends BaseMapper<Assessment, AssessmentResource, Long> {

        @Mappings({
                @Mapping(source = "processStatus", target = "status"),
                @Mapping(source = "processEvent", target = "event"),
                @Mapping(source = "version", target = "lastModified")
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
