package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationStatus;
import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        CompetitionMapper.class,
        ApplicationFinanceMapper.class
    }
)
public abstract class ApplicationStatusMapper  extends BaseMapper<ApplicationStatus, ApplicationStatusResource, Long> {

    public Long mapApplicationStatusToId(ApplicationStatus object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
