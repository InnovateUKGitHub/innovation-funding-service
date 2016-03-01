package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.resource.ResponseResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        QuestionMapper.class,
        ProcessRoleMapper.class,
        AssessorFeedbackMapper.class,
        ApplicationMapper.class
    }
)
public abstract class ResponseMapper extends BaseMapper<Response, ResponseResource, Long> {

    public Long responseToId(Response object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
