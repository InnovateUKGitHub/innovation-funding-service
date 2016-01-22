package com.worth.ifs.form.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.resource.FormInputResource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
        FormInputTypeMapper.class,
        FormInputResponseMapper.class,
        FormValidatorMapper.class
    }
)
public abstract class FormInputMapper {

    @Autowired
    private FormInputRepository repository;

    public abstract FormInputResource mapFormInputToResource(FormInput object);

    public abstract FormInput resourceToFormInput(FormInputResource resource);

    public static final FormInputMapper FormInputMAPPER = Mappers.getMapper(FormInputMapper.class);

    public Long mapFormInputToId(FormInput object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public FormInput mapIdToFormInput(Long id) {
        return repository.findOne(id);
    }

    public Long mapFormInputResourceToId(FormInputResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}