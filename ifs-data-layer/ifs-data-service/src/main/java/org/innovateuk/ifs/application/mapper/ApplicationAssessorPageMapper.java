package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.function.Function;

@Mapper(
        config = GlobalMapperConfig.class
)
public class ApplicationAssessorPageMapper extends PageResourceMapper<AssessmentParticipant, ApplicationAssessorResource> {

    @Autowired
    ApplicationAssessorMapper applicationAssessorMapper;

    public ApplicationAssessorPageResource mapToResource(Page<AssessmentParticipant> source) {
        ApplicationAssessorPageResource result = new ApplicationAssessorPageResource();
        return mapFields(source, result);
    }

    @Override
    protected Function<AssessmentParticipant, ApplicationAssessorResource> contentElementConverter() {
        return applicationAssessorMapper::mapToResource;
    }
}
