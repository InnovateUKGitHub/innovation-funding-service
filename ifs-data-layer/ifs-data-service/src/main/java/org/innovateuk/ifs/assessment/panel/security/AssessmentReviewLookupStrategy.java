package org.innovateuk.ifs.assessment.panel.security;

import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
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

    @PermissionEntityLookupStrategy
    public AssessmentReview getAssessmentReview(final Long id) {
        return assessmentReviewRepository.findOne(id);
    }
}
