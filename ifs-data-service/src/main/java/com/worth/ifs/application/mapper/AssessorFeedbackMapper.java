package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.mapper.ProcessRoleMapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        ResponseMapper.class
    }
)
public abstract class AssessorFeedbackMapper extends BaseMapper<AssessorFeedback, AssessorFeedbackResource> {

    @Autowired
    public void setRepository(CrudRepository<AssessorFeedback, Long> repository) {
        this.repository = repository;
    }

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