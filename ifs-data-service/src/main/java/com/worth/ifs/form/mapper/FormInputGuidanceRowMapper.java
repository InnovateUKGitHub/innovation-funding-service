package com.worth.ifs.form.mapper;

import com.worth.ifs.application.domain.FormInputGuidanceRow;
import com.worth.ifs.application.resource.FormInputGuidanceRowResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FormInputMapper.class
    }
)
public abstract class FormInputGuidanceRowMapper extends BaseMapper<FormInputGuidanceRow, FormInputGuidanceRowResource, Long> {

    public abstract FormInputGuidanceRowResource mapToResource(FormInputGuidanceRow domain);

    public abstract FormInputGuidanceRow mapToDomain(FormInputGuidanceRowResource resource);

    public Long mapFormInputGuidanceRowToId(FormInputGuidanceRow object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}