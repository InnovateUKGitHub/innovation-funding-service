package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.application.resource.ResponseResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        QuestionMapper.class,
        ProcessRoleMapper.class,
        AssessorFeedbackMapper.class,
        ApplicationMapper.class
    }
)
public abstract class ResponseMapper {

    @Autowired
    ResponseRepository repository;

    public abstract ResponseResource mapResponseToResource(Response object);
    public abstract Response resourceToResponse(ResponseResource resource);

    public Long responseToId(Response object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Response idToResponse(Long id){
        return repository.findOne(id);
    }
}
