package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.repository.AssessorFeedbackRepository;
import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        ResponseMapper.class
    }
)
public abstract class AssessorFeedbackMapper {

    @Autowired
    private AssessorFeedbackRepository repository;

    public abstract AssessorFeedbackResource mapAssessorFeedbackToResource(AssessorFeedback object);
    public abstract AssessorFeedback resourceToAssessorFeedback(AssessorFeedbackResource resource);

    public Long mapAssessorFeedbackToId(AssessorFeedback object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public AssessorFeedback mapIdToAssessorFeedback(Long id) {
        return repository.findOne(id);
    }
}