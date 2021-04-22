package org.innovateuk.ifs.assessment.period.transactional;

import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.period.mapper.AssessmentPeriodMapper;
import org.innovateuk.ifs.assessment.period.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of AssessmentPeriod
 */
@Service
public class AssessmentPeriodServiceImpl
       extends AbstractIfsCrudServiceImpl<AssessmentPeriodResource, AssessmentPeriod, Long>
       implements AssessmentPeriodService {

    @Autowired
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Autowired
    private AssessmentPeriodMapper assessmentPeriodMapper;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public ServiceResult<List<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(Long competitionId) {
        return find(assessmentPeriodRepository.findByCompetitionId(competitionId), notFoundError(AssessmentPeriodResource.class, competitionId))
                .andOnSuccessReturn(assessmentPeriodMapper::mapToResource);
    }

    @Override
    protected CrudRepository<AssessmentPeriod, Long> crudRepository() {
        return assessmentPeriodRepository;
    }

    @Override
    protected Class<AssessmentPeriod> getDomainClazz() {
        return AssessmentPeriod.class;
    }

    @Override
    protected AssessmentPeriod mapToDomain(AssessmentPeriod domain, AssessmentPeriodResource resource) {
        Optional<Competition> competition = competitionRepository.findById(resource.getCompetitionId());
        if (domain.getCompetition() == null && competition.isPresent()) {
            domain.setCompetition(competition.get());
        }
        return domain;
    }
}
