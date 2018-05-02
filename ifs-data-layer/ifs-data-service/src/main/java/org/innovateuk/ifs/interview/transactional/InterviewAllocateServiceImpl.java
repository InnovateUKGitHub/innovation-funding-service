package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsResource;
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
public class InterviewAllocateServiceImpl implements InterviewAllocateService  {

    @Autowired
    private InterviewParticipantRepository interviewParticipantRepository;

    @Override
    public ServiceResult<InterviewAssessorAllocateApplicationsPageResource> getAllocateApplicationsOverview(long competitionId,
                                                                                                            Pageable pageable) {
        Page<InterviewAssessorAllocateApplicationsResource> pagedResult = interviewParticipantRepository.getAllocateApplicationsOverview(
                competitionId,
                pageable);

        return serviceSuccess(new InterviewAssessorAllocateApplicationsPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));
    }
}
