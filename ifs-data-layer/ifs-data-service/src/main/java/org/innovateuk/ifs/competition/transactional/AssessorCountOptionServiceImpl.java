package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.mapper.AssessorCountOptionMapper;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;


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
