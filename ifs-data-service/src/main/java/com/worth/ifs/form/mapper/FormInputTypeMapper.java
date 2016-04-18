package com.worth.ifs.form.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.form.resource.FormInputTypeResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FormInputMapper.class
    }
)
public abstract class FormInputTypeMapper  extends BaseMapper<FormInputType, FormInputTypeResource, Long> {

    public Long mapFormInputTypeToId(FormInputType object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}