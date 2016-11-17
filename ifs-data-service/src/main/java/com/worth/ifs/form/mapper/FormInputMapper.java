package com.worth.ifs.form.mapper;

import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.resource.FormInputResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
        FormInputTypeMapper.class,
        FormInputResponseMapper.class,
        FormValidatorMapper.class,
        QuestionMapper.class
    }
)
public abstract class FormInputMapper extends BaseMapper<FormInput, FormInputResource, Long> {

    @Mappings({
            @Mapping(source = "formInputType.title", target = "formInputTypeTitle")
    })
    @Override
    public abstract FormInputResource mapToResource(FormInput domain);

    @Mappings({
            @Mapping(target = "responses", ignore = true),
            @Mapping(target = "guidanceRows", ignore = true)
    })
    @Override
    public abstract FormInput mapToDomain(FormInputResource resource);

    public Long mapFormInputToId(FormInput object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}