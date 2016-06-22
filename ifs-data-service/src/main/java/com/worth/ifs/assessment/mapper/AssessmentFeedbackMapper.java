package com.worth.ifs.assessment.mapper;

import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.assessment.domain.AssessmentFeedback;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;

/**
 * Maps between domain and resource DTO for {@link com.worth.ifs.assessment.domain.AssessmentFeedback}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProcessRoleMapper.class,
                QuestionMapper.class
        }
)
public abstract class AssessmentFeedbackMapper extends BaseMapper<AssessmentFeedback, AssessmentFeedbackResource, Long> {

    public Long mapAssessmentFeedbackToId(final AssessmentFeedback object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}