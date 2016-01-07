package com.worth.ifs.form.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.form.repository.FormValidatorRepository;
import com.worth.ifs.form.resource.FormValidatorResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {

    }
)
public abstract class FormValidatorMapper {

    @Autowired
    private FormValidatorRepository repository;

    public abstract FormValidatorResource mapFormValidatorToResource(FormValidator object) throws ClassNotFoundException;

    public abstract FormValidator resourceToFormValidator(FormValidatorResource resource) throws ClassNotFoundException;

    public Long mapFormValidatorToId(FormValidator object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public FormValidator mapIdToFormValidator(Long id) {
        return repository.findOne(id);
    }

    public Long mapFormValidatorResourceToId(FormValidatorResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}