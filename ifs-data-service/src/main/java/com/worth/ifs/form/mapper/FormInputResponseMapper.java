package com.worth.ifs.form.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

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

    @Mappings({
            @Mapping(source = "formInput.wordCount", target = "formInputMaxWordCount"),
            @Mapping(source = "fileEntry.name", target = "filename"),
            @Mapping(source = "updatedBy.user.id", target = "updatedByUser"),
            @Mapping(source = "updatedBy.user.name", target = "updatedByUserName")
})
    
    @Override
    public abstract FormInputResponseResource mapToResource(FormInputResponse domain);

    public Long mapFormInputResponseToId(FormInputResponse object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}