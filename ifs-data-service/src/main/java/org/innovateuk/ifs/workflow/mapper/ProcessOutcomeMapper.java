package org.innovateuk.ifs.workflow.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.innovateuk.ifs.workflow.resource.ProcessOutcomeResource;
import org.mapstruct.Mapper;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


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

    @Mappings({
            @Mapping(target = "process", ignore = true)
    })
    @Override
    public abstract ProcessOutcome mapToDomain(ProcessOutcomeResource resource);
}
