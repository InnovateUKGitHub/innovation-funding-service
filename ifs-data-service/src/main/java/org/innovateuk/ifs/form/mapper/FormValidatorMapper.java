package org.innovateuk.ifs.form.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.resource.FormValidatorResource;
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

    @Override
    public FormValidatorResource mapToResource(FormValidator domain){
        return mapToResource(domain);
    }
    @Override
    public FormValidator mapToDomain(FormValidatorResource resource){
        return mapToDomain(resource);
    }
}
