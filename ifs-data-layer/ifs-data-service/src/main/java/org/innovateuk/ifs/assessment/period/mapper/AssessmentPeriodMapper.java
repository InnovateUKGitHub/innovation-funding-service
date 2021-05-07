package org.innovateuk.ifs.assessment.period.mapper;

import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.period.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CompetitionMapper.class
        }
)
public abstract class AssessmentPeriodMapper extends BaseResourceMapper<AssessmentPeriod, AssessmentPeriodResource> {
    protected AssessmentPeriodRepository repository;
    @Autowired
    public void setRepository(AssessmentPeriodRepository repository) {
        this.repository = repository;
    }

    @Mapping(target = "competitionId", ignore = true)
    @Override
    public abstract AssessmentPeriodResource mapToResource(AssessmentPeriod domain);

    public Long mapAssessmentPeriodToId(AssessmentPeriod object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public AssessmentPeriod mapIdToDomain(Long id) {
        if(id == null){
            return null;
        }
        return repository.findById(id).orElse(null);
    }
}
