package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.AssessmentPeriodMapper;
import org.innovateuk.ifs.competition.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of AssessmentPeriod
 */
@Service
public class AssessmentPeriodServiceImpl extends BaseTransactionalService implements AssessmentPeriodService {

    @Autowired
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Autowired
    private AssessmentPeriodMapper assessmentPeriodMapper;

    @Override
    public ServiceResult<List<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(Long competitionId) {
        return find(assessmentPeriodRepository.findByCompetitionId(competitionId), notFoundError(AssessmentPeriodResource.class, competitionId))
                .andOnSuccessReturn(assessmentPeriodMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<AssessmentPeriodResource> create(Long competitionId, Integer index) {
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                .andOnSuccess(competition -> {
                    AssessmentPeriod assessmentPeriod = new AssessmentPeriod(competition, index);
                    return serviceSuccess(assessmentPeriodMapper.mapToResource(assessmentPeriodRepository.save(assessmentPeriod)));
                });
    }
}
