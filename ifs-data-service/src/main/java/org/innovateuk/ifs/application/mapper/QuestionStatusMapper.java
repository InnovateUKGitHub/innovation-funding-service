package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        ApplicationMapper.class,
        QuestionMapper.class
    }
)
public abstract class QuestionStatusMapper  extends BaseMapper<QuestionStatus, QuestionStatusResource, Long> {

    @Mappings({
            @Mapping(source = "assignee.user.name", target = "assigneeName"),
            @Mapping(source = "assignee.user.id", target = "assigneeUserId"),
            @Mapping(source = "assignedBy.user.name", target = "assignedByName"),
            @Mapping(source = "assignedBy.user.id", target = "assignedByUserId")
    })
    @Override
    public abstract QuestionStatusResource mapToResource(QuestionStatus domain);

    public Long mapQuestionStatusToId(QuestionStatus object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}
