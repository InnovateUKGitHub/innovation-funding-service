package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.EmployeesAndTurnover;
import org.innovateuk.ifs.finance.resource.EmployeesAndTurnoverResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class EmployeesAndTurnoverMapper extends BaseMapper<EmployeesAndTurnover, EmployeesAndTurnoverResource, Long> {
}
