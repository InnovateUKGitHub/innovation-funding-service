package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Milestone;
import com.worth.ifs.competition.repository.MilestoneRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for operations around the usage and processing of Milestones
 */
public class MilestoneServiceImpl extends BaseTransactionalService implements MilestoneService {

    private static final Log LOG = LogFactory.getLog(MilestoneServiceImpl.class);

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Override
    public ServiceResult<List<Milestone>> getAllMilestoneDatesByCompetitionId(Long id) {

        List<Milestone> milestone = milestoneRepository.findAllByCompetitionId(id);
        if (milestone == null) {
            return serviceFailure(notFoundError(Milestone.class, id));
        }
        return serviceSuccess(milestone);
    }
}
