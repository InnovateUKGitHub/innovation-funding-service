package com.worth.ifs.competition.transactional;

import java.util.List;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.transactional.BaseTransactionalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionServiceImpl extends BaseTransactionalService implements CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CompetitionMapper competitionMapper;

    @Override
    public ServiceResult<CompetitionResource> getCompetitionById(Long id) {
        return serviceSuccess(competitionMapper.mapToResource(competitionRepository.findById(id)));
    }

    @Override
    public ServiceResult<List<CompetitionResource>> findAll() {
        return serviceSuccess((List) competitionMapper.mapToResource(competitionRepository.findAll()));
    }
}
