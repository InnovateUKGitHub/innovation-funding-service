package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.getOrFail;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionServiceImpl extends BaseTransactionalService implements CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public ServiceResult<Competition> getCompetitionById(Long id) {
        return getOrFail(() -> competitionRepository.findOne(id), notFoundError(Competition.class, id));
    }

    @Override
    public ServiceResult<List<Competition>> findAll() {
        return serviceSuccess(competitionRepository.findAll());
    }
}
