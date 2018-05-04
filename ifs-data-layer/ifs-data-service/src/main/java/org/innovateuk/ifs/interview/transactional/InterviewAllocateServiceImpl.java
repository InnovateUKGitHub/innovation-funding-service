package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewResource;
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
    @Autowired
    private InterviewRepository interviewRepository;

    @Override
    public ServiceResult<InterviewAllocateOverviewPageResource> getAllocateApplicationsOverview(long competitionId,
                                                                                                Pageable pageable) {
        Page<InterviewAllocateOverviewResource> pagedResult = interviewParticipantRepository.getAllocateApplicationsOverview(
                competitionId,
                pageable);

        return serviceSuccess(new InterviewAllocateOverviewPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));
    }

    @Override
    public ServiceResult<InterviewApplicationPageResource> getUnallocatedApplications(long competitionId, long assessorUserId, Pageable pageable) {
        Page<InterviewApplicationResource> pagedResult = interviewRepository.findApplicationsNotAssignedToAssessor(
                competitionId,
                assessorUserId, pageable);

        long unallocatedApplications = interviewRepository.countUnallocatedApplications(competitionId, assessorUserId);
        long allocatedApplications = interviewRepository.countAllocatedApplications(competitionId, assessorUserId);

        return serviceSuccess(new InterviewApplicationPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getSize(),
                unallocatedApplications,
                allocatedApplications
        ));
    }

    @Override
    public ServiceResult<InterviewApplicationPageResource> getAllocatedApplications(long competitionId, long assessorUserId, Pageable pageable) {
        Page<InterviewApplicationResource> pagedResult = interviewRepository.findApplicationsAssignedToAssessor(
                competitionId,
                assessorUserId, pageable);

        long unallocatedApplications = interviewRepository.countUnallocatedApplications(competitionId, assessorUserId);
        long allocatedApplications = interviewRepository.countAllocatedApplications(competitionId, assessorUserId);

        return serviceSuccess(new InterviewApplicationPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getSize(),
                unallocatedApplications,
                allocatedApplications
        ));
    }
}
