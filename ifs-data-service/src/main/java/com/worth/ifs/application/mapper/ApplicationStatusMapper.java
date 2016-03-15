package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.finance.mapper.ApplicationFinanceMapper;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
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