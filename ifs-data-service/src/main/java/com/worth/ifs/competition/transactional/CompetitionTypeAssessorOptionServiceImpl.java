package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.mapper.CompetitionTypeAssessorOptionMapper;
import com.worth.ifs.competition.repository.CompetitionTypeAssessorOptionRepository;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;
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
public class CompetitionTypeAssessorOptionServiceImpl extends BaseTransactionalService implements CompetitionTypeAssessorOptionService {

    @Autowired
    private CompetitionTypeAssessorOptionRepository competitionTypeAssessorOptionRepository;

    @Autowired
    private CompetitionTypeAssessorOptionMapper competitionTypeAssessorOptionMapper;

    @Override
    public ServiceResult<List<CompetitionTypeAssessorOptionResource>> findAllByCompetitionType(Long competitionTypeId) {
        return serviceSuccess(simpleMap(competitionTypeAssessorOptionRepository.findByCompetitionTypeId(competitionTypeId), competitionTypeAssessorOptionMapper::mapToResource));
    }
}
