package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.mapper.AssessmentMapper;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link com.worth.ifs.assessment.domain.Assessment}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class AssessmentLookupStrategy {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentMapper assessmentMapper;

    @PermissionEntityLookupStrategy
    public AssessmentResource getAssessmentResource(final Long id) {
        return assessmentMapper.mapToResource(assessmentRepository.findOne(id));
    }

    @PermissionEntityLookupStrategy
    public Assessment getAssessment(final Long id) {
        return assessmentRepository.findOne(id);
    }
}