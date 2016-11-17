package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.mapper.AssessorCountOptionMapper;
import com.worth.ifs.competition.repository.AssessorCountOptionRepository;
import com.worth.ifs.competition.resource.AssessorCountOptionResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;


/**
 * Service for operations around the usage of assessor options for a competition type
 */
@Service
public class AssessorCountOptionServiceImpl extends BaseTransactionalService implements AssessorCountOptionService {

    @Autowired
    private AssessorCountOptionRepository assessorCountOptionRepository;

    @Autowired
    private AssessorCountOptionMapper assessorCountOptionMapper;

    @Override
    public ServiceResult<List<AssessorCountOptionResource>> findAllByCompetitionType(Long competitionTypeId) {
        return serviceSuccess(simpleMap(assessorCountOptionRepository.findByCompetitionTypeId(competitionTypeId), assessorCountOptionMapper::mapToResource));
    }
}
