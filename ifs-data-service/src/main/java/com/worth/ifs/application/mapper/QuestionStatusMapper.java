package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        ApplicationMapper.class,
        QuestionMapper.class
    }
)
public abstract class QuestionStatusMapper {

    @Autowired
    QuestionStatusRepository repository;

    public abstract QuestionStatusResource mapQuestionStatusToResource(QuestionStatus object);
    public abstract QuestionStatus resourceToQuestionStatus(QuestionStatusResource resource);

    public Long mapQuestionStatusToId(QuestionStatus object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public QuestionStatus mapIdToQuestionStatus(Long id) {
        return repository.findOne(id);
    }
}