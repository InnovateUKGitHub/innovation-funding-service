package com.worth.ifs.form.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        FormInputMapper.class,
        ApplicationMapper.class,
        FileEntryMapper.class
    }
)
public abstract class FormInputResponseMapper extends BaseMapper<FormInputResponse, FormInputResponseResource, Long> {

    public Long mapFormInputResponseToId(FormInputResponse object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}