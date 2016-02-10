package com.worth.ifs.form.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.form.repository.FormInputTypeRepository;
import com.worth.ifs.form.resource.FormInputTypeResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        FormInputMapper.class
    }
)
public abstract class FormInputTypeMapper {

    @Autowired
    private FormInputTypeRepository repository;

    public abstract FormInputTypeResource mapFormInputTypeToResource(FormInputType object);

    public abstract FormInputType resourceToFormInputType(FormInputTypeResource resource);

    public Long mapFormInputTypeToId(FormInputType object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public FormInputType mapIdToFormInputType(Long id) {
        return repository.findOne(id);
    }

}