package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.mapper.InterviewMapper;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link Interview}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class InterviewLookupStrategy {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewMapper interviewMapper;

    @PermissionEntityLookupStrategy
    public InterviewResource getAssessmentInterviewResource(final Long id) {
        return interviewMapper.mapToResource(interviewRepository.findOne(id));
    }

    @PermissionEntityLookupStrategy
    public Interview getAssessmentInterview(final Long id) {
        return interviewRepository.findOne(id);
    }
}
