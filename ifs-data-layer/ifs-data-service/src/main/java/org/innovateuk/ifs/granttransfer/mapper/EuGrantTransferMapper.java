package org.innovateuk.ifs.granttransfer.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                EuActionTypeMapper.class
        }
)
public abstract class EuGrantTransferMapper extends BaseMapper<EuGrantTransfer, EuGrantTransferResource, Long> {

    @Mappings({
            @Mapping(target = "projectName", source = "application.name"),
    })
    @Override
    public abstract EuGrantTransferResource mapToResource(EuGrantTransfer project);

    @Mappings({
            @Mapping(target = "calculationSpreadsheet", ignore = true),
            @Mapping(target = "grantAgreement", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "application", ignore = true),
    })
    @Override
    public abstract EuGrantTransfer mapToDomain(EuGrantTransferResource projectResource);

}
