package org.innovateuk.ifs.form.mapper;

import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.mapper.QuestionMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProcessRoleMapper.class,
                FormInputMapper.class,
                ApplicationMapper.class,
                FileEntryMapper.class,
                QuestionMapper.class
        }
)
public abstract class FormInputResponseMapper extends BaseMapper<FormInputResponse, FormInputResponseResource, Long> {

    @Mappings({
            @Mapping(source = "formInput.question", target = "question"),
            @Mapping(source = "formInput.wordCount", target = "formInputMaxWordCount"),
            @Mapping(source = "fileEntry.name", target = "filename"),
            @Mapping(source = "fileEntry.filesizeBytes", target = "filesizeBytes"),
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
