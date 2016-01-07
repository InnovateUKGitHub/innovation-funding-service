package com.worth.ifs.form.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        FormInputMapper.class,
        ApplicationMapper.class,
        FileEntryMapper.class
    }
)
public abstract class FormInputResponseMapper {

    @Autowired
    private FormInputResponseRepository repository;

    public abstract FormInputResponseResource mapFormInputResponseToResource(FormInputResponse object);

    public abstract FormInputResponse resourceToFormInputResponse(FormInputResponseResource resource);


    public Long mapFormInputResponseToId(FormInputResponse object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public FormInputResponse mapIdToFormInputResponse(Long id) {
        return repository.findOne(id);
    }

    public Long mapFormInputResponseResourceToId(FormInputResponseResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}