package org.innovateuk.ifs.form.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.form.domain.GuidanceRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FormInputMapper.class
    }
)
public abstract class GuidanceRowMapper extends BaseMapper<GuidanceRow, GuidanceRowResource, Long> {

    public abstract GuidanceRowResource mapToResource(GuidanceRow domain);

    @Mapping(target = "formInput", ignore = true)
    public abstract GuidanceRow mapToDomain(GuidanceRowResource resource);

    public Long mapFormInputGuidanceRowToId(GuidanceRow object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
