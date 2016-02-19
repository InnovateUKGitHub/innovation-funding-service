package com.worth.ifs.workflow.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {

    }
)
public abstract class ProcessOutcomeMapper  extends BaseMapper<ProcessOutcome, ProcessOutcomeResource, Long> {

    public Long mapProcessOutcomeToId(ProcessOutcome object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}