package com.worth.ifs.form.mapper;

import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.resource.FormInputResource;
import org.mapstruct.Mapper;

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

    public Long mapFormInputToId(FormInput object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}