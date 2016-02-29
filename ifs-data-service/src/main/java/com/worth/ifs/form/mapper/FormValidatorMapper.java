package com.worth.ifs.form.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.form.resource.FormValidatorResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {

    }
)
public abstract class FormValidatorMapper extends BaseMapper<FormValidator, FormValidatorResource, Long> {

    public Long mapFormValidatorToId(FormValidator object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }


    public FormValidatorResource mapToResource(FormValidator domain){
        return mapToResource(domain);
    }
    public FormValidator mapToDomain(FormValidatorResource resource){
        return mapToDomain(resource);
    }
}