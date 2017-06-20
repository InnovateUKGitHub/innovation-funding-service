package org.innovateuk.ifs.form.mapper;

import org.innovateuk.ifs.application.mapper.QuestionMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
        FormInputResponseMapper.class,
        FormValidatorMapper.class,
        QuestionMapper.class,
        GuidanceRowMapper.class
    }
)
public abstract class FormInputMapper extends BaseMapper<FormInput, FormInputResource, Long> {

    @Mappings({
            @Mapping(target = "responses", ignore = true),
            @Mapping(target = "guidanceRows", ignore = true),
            @Mapping(target = "active", ignore = true)
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
