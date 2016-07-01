package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.mapper.AssessmentFeedbackMapper;
import com.worth.ifs.assessment.repository.AssessmentFeedbackRepository;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link com.worth.ifs.assessment.domain.AssessmentFeedback}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class AssessmentFeedbackLookupStrategy {

    @Autowired
    private AssessmentFeedbackRepository assessmentFeedbackRepository;

    @Autowired
    private AssessmentFeedbackMapper assessmentFeedbackMapper;

    @PermissionEntityLookupStrategy
    public AssessmentFeedbackResource getAssessmentFeedbackResource(final Long id){
        return assessmentFeedbackMapper.mapToResource(assessmentFeedbackRepository.findOne(id));
    }

}