package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.mapper.AssessmentMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link org.innovateuk.ifs.assessment.domain.Assessment}, used for permissioning.
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
