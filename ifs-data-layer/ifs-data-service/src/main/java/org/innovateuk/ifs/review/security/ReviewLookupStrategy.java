package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.mapper.ReviewMapper;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link Review}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class ReviewLookupStrategy {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @PermissionEntityLookupStrategy
    public ReviewResource getAssessmentReviewResource(final Long id) {
        return reviewMapper.mapToResource(reviewRepository.findById(id).orElse(null));
    }

    @PermissionEntityLookupStrategy
    public Review getAssessmentReview(final Long id) {
        return reviewRepository.findById(id).orElse(null);
    }
}
