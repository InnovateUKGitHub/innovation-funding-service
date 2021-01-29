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
    public ServiceResult<AssessmentPeriodResource> getAssessmentPeriodByCompetitionIdAndIndex(Long competitionId, Integer index) {
        return find(assessmentPeriodRepository.findByCompetitionIdAndIndex(competitionId, index), notFoundError(AssessmentPeriodResource.class, competitionId, index))
                .andOnSuccess(assessmentPeriod -> serviceSuccess(assessmentPeriodMapper.mapToResource(assessmentPeriod)));
    }

    @Override
    @Transactional
    public ServiceResult<AssessmentPeriodResource> create(Long competitionId, Integer index) {
        Competition competition = competitionRepository.findById(competitionId).orElse(null);

        AssessmentPeriod assessmentPeriod = new AssessmentPeriod(competition, index);
        return serviceSuccess(assessmentPeriodMapper.mapToResource(assessmentPeriodRepository.save(assessmentPeriod)));
    }
}
