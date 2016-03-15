package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        ResponseMapper.class
    }
)
public abstract class AssessorFeedbackMapper extends BaseMapper<AssessorFeedback, AssessorFeedbackResource, Long> {

    public Long mapAssessorFeedbackToId(AssessorFeedback object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}