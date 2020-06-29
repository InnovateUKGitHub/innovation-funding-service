package org.innovateuk.ifs.form.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CompetitionMapper.class,
        FormValidatorMapper.class,
        QuestionMapper.class,
        GuidanceRowMapper.class,
        FileEntryMapper.class,
        MultipleChoiceOptionMapper.class
    }
)
public abstract class FormInputMapper extends BaseMapper<FormInput, FormInputResource, Long> {

    @Mappings({
            @Mapping(target = "guidanceRows", ignore = true),
            @Mapping(target = "multipleChoiceOptions", ignore = true),
            @Mapping(target = "active", ignore = true),
            @Mapping(target = "file", ignore = true)
    })
    @Override
    public abstract FormInput mapToDomain(FormInputResource resource);

    @Override
    public abstract FormInputResource mapToResource(FormInput domain);

    public Long mapFormInputToId(FormInput object) {
        if (object == null) {
            return null;
        }

        return object.getId();
    }
}
