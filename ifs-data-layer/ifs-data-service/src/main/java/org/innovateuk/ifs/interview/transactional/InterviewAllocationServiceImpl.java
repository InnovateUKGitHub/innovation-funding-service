package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for allocating applications to assessors in interview panels
 */
@Service
@Transactional
public class InterviewAllocationServiceImpl implements InterviewAllocationService {

    @Autowired
    private InterviewParticipantRepository interviewParticipantRepository;

    @Override
    public ServiceResult<InterviewAcceptedAssessorsPageResource> getAllocateApplicationsOverview(long competitionId,
                                                                                                 Pageable pageable) {
        Page<InterviewAcceptedAssessorsResource> pagedResult = interviewParticipantRepository.getInterviewAllocateApplicationsOverviewByCompetition(
                competitionId,
                pageable);

        return serviceSuccess(new InterviewAcceptedAssessorsPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));
    }
}
