package org.innovateuk.ifs.assessment.interview.security;

import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterview;
import org.innovateuk.ifs.assessment.interview.mapper.AssessmentInterviewMapper;
import org.innovateuk.ifs.assessment.interview.repository.AssessmentInterviewRepository;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link AssessmentInterview}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class AssessmentInterviewLookupStrategy {

    @Autowired
    private AssessmentInterviewRepository assessmentInterviewRepository;

    @Autowired
    private AssessmentInterviewMapper assessmentInterviewMapper;

    @PermissionEntityLookupStrategy
    public AssessmentInterviewResource getAssessmentInterviewResource(final Long id) {
        return assessmentInterviewMapper.mapToResource(assessmentInterviewRepository.findOne(id));
    }

    @PermissionEntityLookupStrategy
    public AssessmentInterview getAssessmentInterview(final Long id) {
        return assessmentInterviewRepository.findOne(id);
    }
}
