package org.innovateuk.ifs.form.mapper;

import org.innovateuk.ifs.application.mapper.QuestionMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Set;

import static java.util.Collections.emptySet;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

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
            @Mapping(target = "active", ignore = true),
            @Mapping(target = "allowedFileTypes", expression = "java(mapToFileTypesString(resource))")
    })
    @Override
    public abstract FormInput mapToDomain(FormInputResource resource);

    @Mappings({
            @Mapping(target = "allowedFileTypes", expression = "java(mapToFileTypeEnums(domain))")
    })
    @Override
    public abstract FormInputResource mapToResource(FormInput domain);

    public Long mapFormInputToId(FormInput object) {
        if (object == null) {
            return null;
        }

        return object.getId();
    }

    public Set<FileTypeCategory> mapToFileTypeEnums(FormInput formInput) {
        if (formInput.getAllowedFileTypes() == null) {
            return emptySet();
        }

        return simpleMapSet(formInput.getAllowedFileTypes().split(","), FileTypeCategory::fromDisplayName);
    }

    public String mapToFileTypesString(FormInputResource formInputResource) {
        if (formInputResource.getAllowedFileTypes() == null) {
            return null;
        }

        return String.join(",",
                simpleMap(formInputResource.getAllowedFileTypes(),
                        FileTypeCategory::getDisplayName));
    }
}
