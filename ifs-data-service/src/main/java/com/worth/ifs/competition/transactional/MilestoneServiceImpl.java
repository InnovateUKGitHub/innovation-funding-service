package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Milestone;
import com.worth.ifs.competition.mapper.MilestoneMapper;
import com.worth.ifs.competition.repository.MilestoneRepository;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for operations around the usage and processing of Milestones
 */
@Service
public class MilestoneServiceImpl extends BaseTransactionalService implements MilestoneService {

    private static final Log LOG = LogFactory.getLog(MilestoneServiceImpl.class);

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private MilestoneMapper milestoneMapper;



    @Override
    public ServiceResult<List<MilestoneResource>> getAllDatesByCompetitionId(Long id) {
        return serviceSuccess ((List) milestoneMapper.mapToResource(milestoneRepository.findAllDatesByCompetitionId(id)));
    }

    @Override
    public ServiceResult<MilestoneResource> update(Long id, MilestoneResource milestones) {
        Milestone milestone = milestoneMapper.mapToDomain(milestones);
        milestone = milestoneRepository.save(milestone);
        return serviceSuccess(milestoneMapper.mapToResource(milestone));
    }

    @Override
    public ServiceResult<MilestoneResource> create() {
        Milestone milestone = new Milestone();
        //todo save all the milestones in list
        return serviceSuccess(milestoneMapper.mapToResource(milestoneRepository.save(milestone)));
    }
}
