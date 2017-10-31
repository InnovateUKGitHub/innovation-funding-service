package org.innovateuk.ifs.setup.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.setup.domain.SetupStatus;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class SetupStatusMapper extends BaseMapper<SetupStatus, SetupStatusResource, Long> {}
