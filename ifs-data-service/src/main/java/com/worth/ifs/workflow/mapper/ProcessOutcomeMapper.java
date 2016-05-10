package com.worth.ifs.workflow.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.mapstruct.Mapper;
import com.worth.ifs.assessment.domain.Assessment;


@Mapper(
    config = GlobalMapperConfig.class,
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