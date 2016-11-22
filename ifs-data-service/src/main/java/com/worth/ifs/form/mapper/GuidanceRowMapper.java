package com.worth.ifs.form.mapper;

import com.worth.ifs.application.domain.*;
import com.worth.ifs.commons.mapper.*;
import com.worth.ifs.competition.resource.*;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FormInputMapper.class
    }
)
public abstract class GuidanceRowMapper extends BaseMapper<GuidanceRow, GuidanceRowResource, Long> {

    public abstract GuidanceRowResource mapToResource(GuidanceRow domain);

    public abstract GuidanceRow mapToDomain(GuidanceRowResource resource);

    public Long mapFormInputGuidanceRowToId(GuidanceRow object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}