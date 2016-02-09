package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionServiceImpl extends BaseTransactionalService implements CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public ServiceResult<Competition> getCompetitionById(Long id) {
        return super.getCompetition(id);
    }

    @Override
    public ServiceResult<List<Competition>> findAll() {
        return serviceSuccess(competitionRepository.findAll());
    }
}
