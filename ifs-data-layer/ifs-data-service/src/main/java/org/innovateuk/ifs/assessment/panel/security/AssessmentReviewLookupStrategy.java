package org.innovateuk.ifs.assessment.panel.security;

import org.innovateuk.ifs.assessment.panel.mapper.AssessmentReviewMapper;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link AssessmentReview}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class AssessmentReviewLookupStrategy {

    @Autowired
    private AssessmentReviewRepository assessmentReviewRepository;

    @Autowired
    private AssessmentReviewMapper assessmentReviewMapper;

    @PermissionEntityLookupStrategy
    public AssessmentReviewResource getAssessmentReviewResource(final Long id) {
        return assessmentReviewMapper.mapToResource(assessmentReviewRepository.findOne(id));
    }

    @PermissionEntityLookupStrategy
    public AssessmentReview getAssessmentReview(final Long id) {
        return assessmentReviewRepository.findOne(id);
    }
}
